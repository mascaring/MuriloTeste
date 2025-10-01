import { Component, EventEmitter, Input, Output, OnInit, OnChanges, SimpleChanges } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { StorageService } from '../../../auth/services/storage.service';
import { ConfirmationDialogComponent } from '../confirmation-dialog/confirmation-dialog.component';

export interface TaskFormData {
  id?: number;
  title: string;
  description: string;
  status: 'TO_DO' | 'IN_PROGRESS' | 'DONE';
  dueDate: string;
  userId: number;
}

@Component({
  selector: 'app-task-modal',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, ConfirmationDialogComponent],
  templateUrl: './task-modal.component.html',
  styleUrls: ['./task-modal.component.scss']
})
export class TaskModalComponent implements OnInit, OnChanges {
  @Input() isOpen: boolean = false;
  @Input() taskToEdit: TaskFormData | null = null;
  @Output() isOpenChange = new EventEmitter<boolean>();
  @Output() taskSubmit = new EventEmitter<TaskFormData>();
  @Output() deleteTask = new EventEmitter<number>();

  taskForm: FormGroup;
  showConfirmationDialog: boolean = false;
  statusOptions = [
    { value: 'TO_DO', label: 'A Fazer' },
    { value: 'IN_PROGRESS', label: 'Em Progresso' },
    { value: 'DONE', label: 'Concluído' }
  ];

  constructor(
    private fb: FormBuilder,
    private storageService: StorageService
  ) {
    this.taskForm = this.fb.group({
      title: ['', [Validators.required, Validators.minLength(3)]],
      description: [''],
      status: ['TO_DO', Validators.required],
      dueDate: ['', [Validators.required, this.futureDateValidator]],
      userId: [this.storageService.getUserId(), Validators.required]
    });
  }

  ngOnInit(): void {
    if (this.taskToEdit) {
      this.populateForm();
    }
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['taskToEdit']) {
      if (this.taskToEdit) {
        setTimeout(() => {
          this.populateForm();
        }, 0);
      }
    }
    
    if (changes['isOpen'] && this.isOpen && !this.taskToEdit) {
      this.resetForm();
    }
  }

  private futureDateValidator(control: any) {
    if (!control.value) return null;
    
    const selectedDate = new Date(control.value);
    const today = new Date();
    today.setHours(0, 0, 0, 0);
    
    if (selectedDate <= today) {
      return { pastDate: true };
    }
    return null;
  }

  private populateForm(): void {
    if (this.taskToEdit) {
      console.log('Populando formulário com dados:', this.taskToEdit);
      
      // Formatar a data para o input datetime-local
      const dueDate = new Date(this.taskToEdit.dueDate);
      const formattedDate = dueDate.toISOString().slice(0, 16);
      
      console.log('Data formatada:', formattedDate);
      
      this.taskForm.patchValue({
        title: this.taskToEdit.title,
        description: this.taskToEdit.description || '',
        status: this.taskToEdit.status,
        dueDate: formattedDate,
        userId: this.taskToEdit.userId
      });
      
      console.log('Formulário após patch:', this.taskForm.value);
    }
  }

  private resetForm(): void {
    this.taskForm.reset();
    this.taskForm.patchValue({
      status: 'TO_DO',
      userId: this.storageService.getUserId()
    });
  }

  onSubmit(): void {
    if (this.taskForm.valid) {
      const formData: TaskFormData = {
        ...this.taskForm.value,
        id: this.taskToEdit?.id
      };
      this.taskSubmit.emit(formData);
      this.closeModal();
    }
  }

  onDelete(): void {
    if (this.taskToEdit?.id != null) {
      this.showConfirmationDialog = true;
    }
  }

  onDeleteConfirmed(): void {
    if (this.taskToEdit?.id != null) {
      this.deleteTask.emit(this.taskToEdit.id);
      this.showConfirmationDialog = false;
      this.closeModal();
    }
  }

  onDeleteCancelled(): void {
    this.showConfirmationDialog = false;
  }

  closeModal(): void {
    this.isOpen = false;
    this.isOpenChange.emit(false);
    this.resetForm();
  }

  onBackdropClick(event: Event): void {
    if (event.target === event.currentTarget) {
      this.closeModal();
    }
  }

  get modalTitle(): string {
    return this.taskToEdit ? 'Editar Tarefa' : 'Nova Tarefa';
  }

  get submitButtonText(): string {
    return this.taskToEdit ? 'Salvar Alterações' : 'Criar Tarefa';
  }

  get dueDateError(): string {
    const dueDateControl = this.taskForm.get('dueDate');
    if (dueDateControl?.errors?.['pastDate']) {
      return 'A data de vencimento deve ser posterior à data atual';
    }
    return '';
  }
}
