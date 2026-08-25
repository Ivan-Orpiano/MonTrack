package net.codejava.budget_tracker.repository;


import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.BatchUpdateSpreadsheetRequest;
import com.google.api.services.sheets.v4.model.DeleteDimensionRequest;
import com.google.api.services.sheets.v4.model.DimensionRange;
import com.google.api.services.sheets.v4.model.Request;
import com.google.api.services.sheets.v4.model.Sheet;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import com.google.api.services.sheets.v4.model.ValueRange;

import jakarta.annotation.PostConstruct;
import net.codejava.budget_tracker.exception.GoogleSheetsException;
import net.codejava.budget_tracker.exception.ResourceNotFoundException;
import net.codejava.budget_tracker.model.Transaction;
import net.codejava.budget_tracker.model.TransactionType;


/**
 * CRUD operations against a single Google Sheet tab, using it as a lightweight
 * database. Each transaction is one row; column layout is fixed:
 *
 * <pre>
 *   A       B      C     D         E             F
 *   ID  |  Date | Type | Category | Description | Amount
 * </pre>
 *
 * <p>Row 1 is always the header row (auto-created on startup if missing), so data
 * lives in rows 2..N. Because the Sheets API has no concept of "update/delete by
 * value", every write first scans the sheet to resolve the target row number.
 * This keeps the implementation simple and is perfectly adequate for a personal
 * budget tracker's data volumes; it is not intended for high-throughput or
 * highly concurrent use (see README for this trade-off).</p>
 */

@Repository
public class GoogleSheetsTransactionRepository extends TransactionRepository {
    
    private static final List <Object> HEADER_ROW  = List.of("ID", "Date", "Type", "Category", "Description", "Amount");
    private static final String HEADER_RANGE_TEMPLATE = "%s!A1:F1";
    private static final String DATA_RANGE_TEMPLATE = "%s!A2:F";
    private static final String APPEND_RANGE_TEMPLATE = "%s!A:F";

    private final Sheets sheetsService;

    @Value("${google.sheets.spreadsheet-id}")
    private String spreadsheetId;

    @Value("${google.sheets.sheet-name:Transactions}")
    private String sheetName;

    private Integer cachedSheetId;

    public GoogleSheetsTransactionRepository(Sheets sheetsService) {
        this.sheetsService = sheetsService;
    }

        /** Ensures the header row exists once at startup, so writes don't need to check every time. */
   @PostConstruct
    public void init() {
        try {
            ensureHeaderExists();
        } catch (IOException e) {
            throw new GoogleSheetsException(
                    "Could not initialize the '" + sheetName + "' sheet. Check GOOGLE_SHEETS_SPREADSHEET_ID, " +
                            "GOOGLE_SHEETS_SHEET_NAME, and that the sheet is shared with the service account.", e);
        }
    }

    @Override
    public List<Transaction> findAll() {
        List<List<Object>> rows = readAllRows();
        List<Transaction> transactions = new ArrayList<>();
        for (int i = 0; i < rows.size(); i++) {
            Transaction transaction = rowToTransaction(rows.get(i), i + 2);
            if (transaction != null) {
                transactions.add(transaction);
            }
        }
        return transactions;
    }

    @Override
    public Optional<Transaction> findById(String id) {
        return findAll().stream().filter(t -> t.getId().equals(id)).findFirst();
    }

    @Override
    public Transaction save(Transaction transaction) {
        try {
            ValueRange body = new ValueRange().setValues(List.of(transactionToRow(transaction)));
            sheetsService.spreadsheets().values()
                    .append(spreadsheetId, APPEND_RANGE_TEMPLATE.formatted(sheetName), body)
                    .setValueInputOption("USER_ENTERED")
                    .setInsertDataOption("INSERT_ROWS")
                    .execute();
            return transaction;
        } catch (IOException e) {
            throw new GoogleSheetsException("Failed to save the transaction to Google Sheets", e);
        }
    }

    @Override
    public Transaction update(String id, Transaction transaction) {
        int rowNumber = findRowNumberById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with id: " + id));
        try {
            String range = "%s!A%d:F%d".formatted(sheetName, rowNumber, rowNumber);
            ValueRange body = new ValueRange().setValues(List.of(transactionToRow(transaction)));
            sheetsService.spreadsheets().values()
                    .update(spreadsheetId, range, body)
                    .setValueInputOption("USER_ENTERED")
                    .execute();
            return transaction;
        } catch (IOException e) {
            throw new GoogleSheetsException("Failed to update the transaction in Google Sheets", e);
        }
    }

    @Override
    public void deleteById(String id) {
        int rowNumber = findRowNumberById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with id: " + id));
        try {
            int sheetId = resolveSheetId();
            DeleteDimensionRequest deleteRequest = new DeleteDimensionRequest()
                    .setRange(new DimensionRange()
                            .setSheetId(sheetId)
                            .setDimension("ROWS")
                            .setStartIndex(rowNumber - 1) // 0-indexed, inclusive
                            .setEndIndex(rowNumber));      // 0-indexed, exclusive
            BatchUpdateSpreadsheetRequest batchRequest = new BatchUpdateSpreadsheetRequest()
                    .setRequests(List.of(new Request().setDeleteDimension(deleteRequest)));
            sheetsService.spreadsheets().batchUpdate(spreadsheetId, batchRequest).execute();
        } catch (IOException e) {
            throw new GoogleSheetsException("Failed to delete the transaction from Google Sheets", e);
        }
    }

    // ---------------------------------------------------------------------
    // Helpers
    // ---------------------------------------------------------------------

    private List<List<Object>> readAllRows() {
        try {
            ValueRange response = sheetsService.spreadsheets().values()
                    .get(spreadsheetId, DATA_RANGE_TEMPLATE.formatted(sheetName))
                    .execute();
            List<List<Object>> values = response.getValues();
            return values != null ? values : new ArrayList<>();
        } catch (IOException e) {
            throw new GoogleSheetsException("Failed to read transactions from Google Sheets", e);
        }
    }

    /** Returns the 1-indexed sheet row number (accounting for the header row) for a given ID. */
    private Optional<Integer> findRowNumberById(String id) {
        List<List<Object>> rows = readAllRows();
        for (int i = 0; i < rows.size(); i++) {
            List<Object> row = rows.get(i);
            if (!row.isEmpty() && id.equals(String.valueOf(row.get(0)))) {
                return Optional.of(i + 2); // +1 for 0-index -> 1-index, +1 for the header row
            }
        }
        return Optional.empty();
    }

    private void ensureHeaderExists() throws IOException {
        ValueRange response = sheetsService.spreadsheets().values()
                .get(spreadsheetId, HEADER_RANGE_TEMPLATE.formatted(sheetName))
                .execute();
        List<List<Object>> values = response.getValues();
        if (values == null || values.isEmpty()) {
            ValueRange body = new ValueRange().setValues(List.of(HEADER_ROW));
            sheetsService.spreadsheets().values()
                    .update(spreadsheetId, HEADER_RANGE_TEMPLATE.formatted(sheetName), body)
                    .setValueInputOption("RAW")
                    .execute();
        }
    }

    /** The numeric sheetId (tab identifier) is required for row-delete requests; cached after first lookup. */
    private int resolveSheetId() throws IOException {
        if (cachedSheetId != null) {
            return cachedSheetId;
        }
        Spreadsheet spreadsheet = sheetsService.spreadsheets().get(spreadsheetId).execute();
        for (Sheet sheet : spreadsheet.getSheets()) {
            if (sheet.getProperties().getTitle().equals(sheetName)) {
                cachedSheetId = sheet.getProperties().getSheetId();
                return cachedSheetId;
            }
        }
        throw new GoogleSheetsException("Sheet tab '" + sheetName + "' was not found in the target spreadsheet");
    }

    private List<Object> transactionToRow(Transaction t) {
        List<Object> row = new ArrayList<>();
        row.add(t.getId());
        row.add(t.getDate().toString());
        row.add(((Enum<TransactionType>) t.getType()).name());
        row.add(t.getCategory());
        row.add(t.getDescription());
        row.add(t.getAmount().toPlainString());
        return row;
    }

    /** Returns null (and skips the row) for blank trailing rows rather than failing the whole read. */
    private Transaction rowToTransaction(List<Object> row, int rowNumber) {
        if (row.isEmpty() || row.get(0) == null || String.valueOf(row.get(0)).isBlank()) {
            return null;
        }
        try {
            String id = String.valueOf(row.get(0));
            LocalDate date = LocalDate.parse(String.valueOf(cell(row, 1, "")));
            TransactionType type = TransactionType.valueOf(String.valueOf(cell(row, 2, "")).trim().toUpperCase());
            String category = String.valueOf(cell(row, 3, ""));
            String description = String.valueOf(cell(row, 4, ""));
            BigDecimal amount = new BigDecimal(String.valueOf(cell(row, 5, "0")));
            return new Transaction(id, date, type, category, description, amount);
        } catch (RuntimeException e) {
            throw new GoogleSheetsException("Malformed transaction data found in sheet row " + rowNumber, e);
        }
    }

    private Object cell(List<Object> row, int index, Object fallback) {
        return index < row.size() ? row.get(index) : fallback;
    }


}
