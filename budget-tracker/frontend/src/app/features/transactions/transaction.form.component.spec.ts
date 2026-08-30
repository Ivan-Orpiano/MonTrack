import {ComponentFixture, TestBed } from '@angular/core/testing';
import {provideHttpClient} from '@angular/common/http';
import { provideHttpClientTesting }  from '@angular/common/http/testing';
import { ActivatedRoute, convertToParamMap } from '@angular/router';
import { TransactionFormComponent } from './transaction.form.component';


describe('TransactionFormComponent', () => {
    let component: TransactionFormComponent;
    let fixture: ComponentFixture<TransactionFormComponent>;

    beforeEach(async () => {
        await TestBed.configureTestingModule({
            imports: [TransactionFormComponent],
            providers: [
                provideHttpClient(),
                provideHttpClientTesting(),
                {
                    provide: ActivatedRoute,
                    useValue: {snapshot: { paramMap: convertToParamMap({})}}
                }
            ]
        }).compileComponents();
  
    fixture = TestBed.createComponent(TransactionFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
});

    it ('should create in "add" mode when no route id is present', () => {
    expect(component).toBeTruthy();
    expect(component.isEditMode()).toBeFalse(); 
    });

    it('should mark the form invalid when required fields are empty', ()=> {
        component.form.patchValue({category: '', description: '', amount: null });
        expect(component.form.invalid).toBeTrue();
    });

    it('should mark the form valid with correct data', () => {
        component.form.patchValue({
        date: '2026-01-10',
        type: 'EXPENSE',
        category: 'Food',
        description: 'Lunch',
        amount: 12.5
        });
        expect (component.form.valid).toBeTrue();
    });

    it('should require a positive amount', () => {
        component.form.patchValue({amount: 0});
        expect(component.f['amount'].valid).toBeFalse();
    });

    it('should not submit an invalid form', () => {
        component.form.patchValue({category: ''});
        component.onSubmit();
        expect(component.saving()).toBeFalse();
    });

  });
