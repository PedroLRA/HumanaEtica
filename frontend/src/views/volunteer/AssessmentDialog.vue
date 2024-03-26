<template>
  <v-dialog v-model="dialog" persistent width="1300">
    <v-card>
      <v-card-title>
        <span class="headline">
          {{ 'New Assessment' }}
        </span>
      </v-card-title>
      <v-card-text>
        <v-form ref="form" lazy-validation>
          <v-row>
            <v-col cols="12" md="4" sm="6">
              <v-text-field
                v-model="newAssessment.review"
                :rules="[(v) => !!v || 'Review is required']"
                data-cy="reviewInput"
                label="*Review"
                required
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
          @click="$emit('close-assessment-dialog')"
        >
          Close
        </v-btn>
        <v-btn
          :disabled="!canSave"
          color="blue-darken-1"
          data-cy="saveAssessment"
          variant="text"
          @click="saveAssessment"
        >
          Save
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>
<script lang="ts">
import { Vue, Component, Prop, Model } from 'vue-property-decorator';
import Assessment from '@/models/assessment/Assessment';
import RemoteServices from '@/services/RemoteServices';
import VueCtkDateTimePicker from 'vue-ctk-date-time-picker';
import 'vue-ctk-date-time-picker/dist/vue-ctk-date-time-picker.css';
import { ISOtoString } from '@/services/ConvertDateService';
import Activity from '@/models/activity/Activity';

Vue.component('VueCtkDateTimePicker', VueCtkDateTimePicker);
@Component({
  methods: { ISOtoString },
})
export default class AssessmentDialog extends Vue {
  @Model('dialog', Boolean) dialog!: boolean;
  @Prop({ type: Activity, required: true }) readonly activity!: Activity;

  newAssessment: Assessment = new Assessment();

  cypressCondition: boolean = false;

  get canSave(): boolean {
    return (
      this.cypressCondition ||
      (!!this.newAssessment.review &&
        !!this.newAssessment.volunteerName &&
        !!this.newAssessment.reviewDate &&
        this.newAssessment.review.length >= 10)
    );
  }

  async created() {
    this.newAssessment = new Assessment();
    this.newAssessment.volunteerName = this.$store.getters.getUser.name;
    this.newAssessment.reviewDate = ISOtoString(new Date().toString());
  }

  async saveAssessment() {
    if ((this.$refs.form as Vue & { validate: () => boolean }).validate()) {
      try {
        const result = await RemoteServices.registerAssessment(
          this.$store.getters.getUser.id,
          this.newAssessment,
          this.activity.institution.id,
        );
        this.$emit('save-assessment', result);
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
  }
}
</script>

<style lang="scss" scoped></style>
