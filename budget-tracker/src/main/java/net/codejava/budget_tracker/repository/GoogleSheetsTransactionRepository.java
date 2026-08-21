package net.codejava.budget_tracker.repository;


import net.codejava.budget_tracker.exception.GoogleSheetsException;
import net.codejava.budget_tracker.exception.ResourceNotFoundException;
import net.codejava.budget_tracker.model.Transaction;
import net.codejava.budget_tracker.model.TransactionType;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.BatchUpdateSpreadsheetRequest;
import com.google.api.services.sheets.v4.model.DeleteDimensionRequest;
import com.google.api.services.sheets.v4.model.DimensionRange;
import com.google.api.services.sheets.v4.model.Request;
import com.google.api.services.sheets.v4.model.Sheet;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import com.google.api.services.sheets.v4.model.ValueRange;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


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


public class GoogleSheetsTransactionRepository implements TransactionRepository {
    
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
        try{
            ensureHeaderExists();
        } catch (IOException e) {
            throw new GoogleSheetsException( 
                 "Could not initialize the '" + sheetName + "' sheet. Check GOOGLE_SHEETS_SPREADSHEET_ID, " +
                "GOOGLE_SHEETS_SHEET_NAME, and that the sheet is shared with the service account.", e);
        }
    }
}
