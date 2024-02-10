import { Component, EventEmitter, OnInit, Output } from '@angular/core';

@Component({
  standalone: true,
  selector: 'month-selection-menu',
  templateUrl: './month-selection-menu.component.html',
  styleUrl: './month-selection-menu.component.css'
})
export class MonthSelectionMenuComponent  implements OnInit {
  menuOpened: boolean;
  monthsSelected?: number[];
  allMonths?: HTMLInputElement;
  may?: HTMLInputElement;
  june?: HTMLInputElement;
  july?: HTMLInputElement;
  @Output() selectedMonths: EventEmitter<number[]> =
      new EventEmitter<number[]>();
  
  constructor() {
    this.menuOpened = false;
  }
  
  openMenu(): void {
    this.menuOpened = !this.menuOpened;
    
    if (!this.menuOpened) {
      this.selectedMonths.emit(this.monthsSelected!);
    }
  }

  ngOnInit(): void {
    this.initMonthCheckboxes();
    this.selectAllMonthsOnInit();
  }

  initMonthCheckboxes(): void {
    this.allMonths = document.getElementById("all") as HTMLInputElement;
    this.may = document.getElementById("may") as HTMLInputElement;
    this.june = document.getElementById("june") as HTMLInputElement;
    this.july = document.getElementById("july") as HTMLInputElement;
  }

  selectAllMonthsOnInit(): void {
    this.allMonths!.checked = true;
    this.may!.checked = true;
    this.june!.checked = true;
    this.july!.checked = true;
    this.monthsSelected = [5, 6, 7];
  }

  selectAllMonths(): void {
    this.may!.checked = this.allMonths!.checked;
    this.june!.checked = this.allMonths!.checked;
    this.july!.checked = this.allMonths!.checked;
    
    if (this.may!.checked) {
      this.monthsSelected = [5, 6, 7];
    } else {
      this.monthsSelected = [];
    }
  }

  selectMonth(month: number): void {
    this.monthsSelected = [];

    if (this.may!.checked) {
      this.monthsSelected!.push(5);
    }

    if (this.june!.checked) {
      this.monthsSelected!.push(6);
    }

    if (this.july!.checked) {
      this.monthsSelected!.push(7);
    }

    if (this.monthsSelected.length < 3) {
      this.allMonths!.checked = false;
    }
  }
}