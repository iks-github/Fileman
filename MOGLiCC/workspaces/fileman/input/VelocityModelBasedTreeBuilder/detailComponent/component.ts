/*
 * Copyright 2020 IKS Gesellschaft fuer Informations- und Kommunikationssysteme mbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import { Component, OnInit } from '@angular/core';
import { FormGroup, FormControl, Validators, AbstractControl, ValidationErrors} from '@angular/forms';
import { Router } from '@angular/router';
import { map } from 'rxjs/operators';
import { Observable } from 'rxjs';
import { Utils } from 'src/app/common/Utils';
import { <<Type>> } from 'src/app/common/domainobjects/gen/<<Type>>';
import { FilemanLoginService } from 'src/app/services/fileman-login.service';
import { <<Type>>Service } from 'src/app/services/fileman-<<type>>-service';

@Component({
  selector: 'fileman-<<type>>-details',
  templateUrl: './fileman-<<type>>-details.component.html',
  styleUrls: ['./fileman-<<type>>-details.component.css']
})
export class <<Type>>DetailsComponent implements OnInit {

  readOnly: boolean;
  metaDataForm: FormGroup;
  currentlyLoggedInUser: any;
  newMode: boolean;
  toEdit: <<Type>>;
  form: any;

  constructor(private router: Router,
              private loginService: FilemanLoginService,
              private <<type>>Service: <<Type>>Service) {
      this.form = this.createFormGroup();
      this.currentlyLoggedInUser = loginService.getCurrentUserName();
  }

  ngOnInit(): void {
    this.readOnly = this.loginService.getCurrentUserRole() === 'Reader';
    this.newMode = this.router.url.endsWith('new');
    if ( ! this.newMode ) {
      const index = this.router.url.lastIndexOf('/') + 1;
      const id = this.router.url.substring(index);
      this.toEdit = this.<<type>>Service.get<<Type>>(id);
      if (this.toEdit == null) {
        alert('No data available for <<type>> "' + id + '"!');
        this.backToOverview();  // no data to edit avaible - happends for page reload - reason unclear
      } else {
        this.setDataToControls(this.toEdit);
      }
    }
  }

  getToolTip() {
    if (! this.readOnly) { return ''; }
    return 'No permission!';
  }

  save() {
    const toSave = this.form.value as <<Type>>;
    console.log('Saving ');
    console.log(toSave);

    if (this.newMode)
    {
      this.<<type>>Service.create(toSave)
          .subscribe(() => {}, error => {
            alert('Error saving new <<type>> "' + toSave.getId() + '"!');
          });
    }
    else
    {
      this.<<type>>Service.update(toSave)
          .subscribe(() => {}, error => {
            alert('Error saving new <<type>> "' + toSave.getId() + '"!');
          });

    }

    this.backToOverview();
  }

  backToOverview() {
    this.router.navigate(['/fileman/overview']);
  }

  cancel() {
    this.backToOverview();
  }

  // The form control block below is generated - do not modify manually!
  // The form control block above is generated - do not modify manually!
}