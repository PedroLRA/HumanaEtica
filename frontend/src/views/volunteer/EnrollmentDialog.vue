<template>
  <v-dialog v-model="dialog" persistent width="1300">
    <v-card>
      <v-card-title>
        <span class="headline"> New Application </span>
      </v-card-title>
      <v-card-text>
        <v-form ref="form" lazy-validation>
          <v-row>
            <v-col cols="12" sm="6" md="4">
              <v-text-field
                label="*Motivation"
                :rules="[
                  (v) => !!v || 'Motivation is required',
                  (v) =>
                    (v && v.length >= 10) ||
                    'Motivation must be at least 10 characters',
                ]"
                required
                v-model="editEnrollment.motivation"
                data-cy="motivationInput"
              ></v-text-field>
            </v-col>
          </v-row>
        </v-form>
      </v-card-text>
      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn
          color="blue-darken-1"
          variant="text"
          @click="$emit('close-enrollment-dialog')"
        >
          Close
        </v-btn>
        <v-btn
        v-if="canSave"
          color="blue-darken-1"
          variant="text"
          @click="createEnrollment"
          data-cy="saveEnrollment"
        >
          Save
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>
<script lang="ts">
import { Vue, Component, Prop, Model } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import VueCtkDateTimePicker from 'vue-ctk-date-time-picker';
import 'vue-ctk-date-time-picker/dist/vue-ctk-date-time-picker.css';
import { ISOtoString } from '@/services/ConvertDateService';
import Enrollment from '@/models/enrollment/Enrollment';

Vue.component('VueCtkDateTimePicker', VueCtkDateTimePicker);
@Component({
  methods: { ISOtoString },
})

export default class EnrollmentDialog extends Vue {
  @Model('dialog', Boolean) dialog!: boolean;
  @Prop({ type: Enrollment, required: true }) readonly enrollment!: Enrollment;

  editEnrollment: Enrollment = new Enrollment();

  cypressCondition: boolean = false;

  async created() {
    this.editEnrollment = new Enrollment(this.enrollment);
  }

  get canSave(): boolean {
    if (this.editEnrollment.motivation === undefined) {
      return false;
    }
    return this.cypressCondition || this.editEnrollment.motivation.length >= 10;
  }

  createEnrollment() {
    console.log('createEnrollment click');
  }
}
</script>

<style scoped lang="scss"></style>
