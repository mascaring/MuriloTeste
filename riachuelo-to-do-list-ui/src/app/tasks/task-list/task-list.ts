// src/app/tasks/task-list/task-list.component.ts
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TaskService, Task } from '../services/task.service';
import { TopMenuComponent } from '../../shared/components/top-menu/top-menu.component';
import { TaskModalComponent, TaskFormData } from '../../shared/components/task-modal/task-modal.component';
import { ToastService } from '../../shared/services/toast.service';
import { StorageService } from '../../auth/services/storage.service';

@Component({
  selector: 'app-task-list',
  standalone: true,
  imports: [CommonModule, FormsModule, TopMenuComponent, TaskModalComponent],
  templateUrl: './task-list.component.html',
  styleUrls: ['./task-list.component.scss']
})
export class TaskListComponent implements OnInit {
  tasks: Task[] = [];
  isModalOpen: boolean = false;
  selectedTask: TaskFormData | null = null;

  constructor(
    private taskService: TaskService,
    private toastService: ToastService,
    private storageService: StorageService
  ) {}

  ngOnInit(): void {
    this.loadTasks();
  }

  loadTasks(): void {
    this.taskService.getTasks().subscribe({
      next: (data: Task[]) => {
        this.tasks = data;
        console.log('Tarefas carregadas:', this.tasks);
      },
      error: (err: any) => {
        console.error('Erro ao carregar tarefas', err);
        this.toastService.showError('Erro ao carregar tarefas');
      }
    });
  }

  openTaskModal(): void {
    this.selectedTask = null;
    this.isModalOpen = true;
  }

  editTask(task: Task): void {
    if (task.id) {
      console.log('Buscando tarefa com ID:', task.id);
      // Buscar dados completos da tarefa via API
      this.taskService.getTaskById(task.id).subscribe({
        next: (taskData: Task) => {
          console.log('Dados recebidos da API:', taskData);
          this.selectedTask = {
            id: taskData.id,
            title: taskData.title,
            description: taskData.description || '',
            status: taskData.status,
            dueDate: taskData.dueDate,
            userId: this.storageService.getUserId() // Usar ID do usuário logado
          };
          console.log('selectedTask definido como:', this.selectedTask);
          this.isModalOpen = true;
        },
        error: (err: any) => {
          console.error('Erro ao buscar dados da tarefa', err);
          this.toastService.showError('Erro ao carregar dados da tarefa');
        }
      });
    }
  }

  onTaskSubmit(taskData: TaskFormData): void {
    if (taskData.id) {
      // Editar tarefa existente
      this.taskService.updateTask(taskData.id, taskData).subscribe({
        next: (data: Task) => {
          const index = this.tasks.findIndex(task => task.id === taskData.id);
          if (index !== -1) {
            this.tasks[index] = data;
          }
          console.log('Tarefa atualizada:', data);
          this.toastService.showSuccess('Tarefa atualizada com sucesso!');
        },
        error: (err: any) => {
          console.error('Erro ao atualizar tarefa', err);
          this.toastService.showError('Erro ao atualizar tarefa');
        }
      });
    } else {
      // Criar nova tarefa
      this.taskService.createTask(taskData).subscribe({
        next: (data: Task) => {
          this.tasks.push(data);
          console.log('Tarefa criada:', data);
          this.toastService.showSuccess('Tarefa criada com sucesso!');
        },
        error: (err: any) => {
          console.error('Erro ao criar tarefa', err);
          this.toastService.showError('Erro ao criar tarefa');
        }
      });
    }
  }

  toggleTask(task: Task): void {
    const newStatus: 'TO_DO' | 'IN_PROGRESS' | 'DONE' = task.status === 'DONE' ? 'TO_DO' : 'DONE';
    const updatedTask = { ...task, status: newStatus };
    
    this.taskService.updateTask(task.id!, updatedTask).subscribe({
      next: (data: Task) => {
        task.status = newStatus;
        console.log('Tarefa atualizada:', data);
        const statusText = newStatus === 'DONE' ? 'concluída' : 'reaberta';
        this.toastService.showSuccess(`Tarefa ${statusText} com sucesso!`);
      },
      error: (err: any) => {
        console.error('Erro ao atualizar tarefa', err);
        this.toastService.showError('Erro ao alterar status da tarefa');
      }
    });
  }

  deleteTask(taskId: number): void {
    this.taskService.deleteTask(taskId).subscribe({
      next: () => {
        this.tasks = this.tasks.filter(task => task.id !== taskId);
        this.toastService.showSuccess('Tarefa excluída com sucesso!');
      },
      error: (err: any) => {
        console.error('Erro ao excluir tarefa', err);
        this.toastService.showError('Erro ao excluir tarefa');
      }
    });
  }

  getStatusLabel(status: string): string {
    const statusMap: { [key: string]: string } = {
      'TO_DO': 'A Fazer',
      'IN_PROGRESS': 'Em Progresso',
      'DONE': 'Concluído'
    };
    return statusMap[status] || status;
  }

  formatDate(dateString: string): string {
    const date = new Date(dateString);
    return date.toLocaleDateString('pt-BR', {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  }
}
