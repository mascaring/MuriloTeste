import { Component, EventEmitter, Input, Output, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { Subject, debounceTime, distinctUntilChanged, takeUntil } from 'rxjs';

export interface TaskFilters {
  status?: string;
  title?: string;
  dueDate?: string;
  createdAt?: string;
}

@Component({
  selector: 'app-task-filters',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './task-filters.component.html',
  styleUrls: ['./task-filters.component.scss']
})
export class TaskFiltersComponent implements OnInit, OnDestroy {
  @Input() isVisible: boolean = false;
  @Output() filtersApplied = new EventEmitter<TaskFilters>();
  @Output() filtersCleared = new EventEmitter<void>();

  filterForm: FormGroup;
  statusOptions = [
    { value: '', label: 'Todos os Status' },
    { value: 'TO_DO', label: 'A Fazer' },
    { value: 'IN_PROGRESS', label: 'Em Progresso' },
    { value: 'DONE', label: 'Concluído' }
  ];

  private destroy$ = new Subject<void>();
  private titleSubject = new Subject<string>();

  constructor(private fb: FormBuilder) {
    this.filterForm = this.fb.group({
      status: [''],
      title: [''],
      dueDate: [''],
      createdAt: ['']
    });
  }

  ngOnInit(): void {
    this.filterForm.get('status')?.valueChanges
      .pipe(takeUntil(this.destroy$))
      .subscribe(() => this.applyFilters());

    this.filterForm.get('dueDate')?.valueChanges
      .pipe(takeUntil(this.destroy$))
      .subscribe(() => this.applyFilters());

    this.filterForm.get('createdAt')?.valueChanges
      .pipe(takeUntil(this.destroy$))
      .subscribe(() => this.applyFilters());

    this.titleSubject
      .pipe(
        debounceTime(500),
        distinctUntilChanged(),
        takeUntil(this.destroy$)
      )
      .subscribe(() => this.applyFilters());

    this.filterForm.get('title')?.valueChanges
      .pipe(takeUntil(this.destroy$))
      .subscribe(value => this.titleSubject.next(value));
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  applyFilters(): void {
    const filters: TaskFilters = {};
    
    if (this.filterForm.get('status')?.value) {
      filters.status = this.filterForm.get('status')?.value;
    }
    
    if (this.filterForm.get('title')?.value?.trim()) {
      filters.title = this.filterForm.get('title')?.value.trim();
    }
    
    if (this.filterForm.get('dueDate')?.value) {
      filters.dueDate = this.filterForm.get('dueDate')?.value;
    }
    
    if (this.filterForm.get('createdAt')?.value) {
      filters.createdAt = this.filterForm.get('createdAt')?.value;
    }
    
    this.filtersApplied.emit(filters);
  }

  clearFilters(): void {
    this.filterForm.reset();
    this.filterForm.patchValue({
      status: '',
      title: '',
      dueDate: '',
      createdAt: ''
    });
    this.filtersCleared.emit();
  }

  get hasActiveFilters(): boolean {
    return !!(
      this.filterForm.get('status')?.value ||
      this.filterForm.get('title')?.value ||
      this.filterForm.get('dueDate')?.value ||
      this.filterForm.get('createdAt')?.value
    );
  }
}
