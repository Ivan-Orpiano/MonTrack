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
    })

    fixture = TestBed.createComponent(TransactionFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
});

it ('should create in "add" mode when no route id is present', () => {
   expect(component).toBeTruthy();
   expect(component.isEditMode()).toBeFalse(); 
});
g
