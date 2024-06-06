import { Component, Inject} from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
@Component({
  selector: 'app-dialog',
  templateUrl: './dialog.component.html',
  styleUrls: ['./dialog.component.css'],
  standalone: true,
  imports: [MatDialogModule, MatButtonModule],
})
export class DialogComponent {
  constructor(@Inject(MAT_DIALOG_DATA) public data: any) { }
}
