<template>
    <v-dialog v-model="dialog" persistent width="500">
      <v-card>
        <v-card-title>
          <span class="headline">Select Participant</span>
        </v-card-title>
        <v-card-text>
          <v-form ref="form" lazy-validation>
            <v-row>
              <v-col cols="12">
                <v-text-field
                  label="Rating"
                  v-model="rating"
                  data-cy="ratingInput"
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
            @click="$emit('close-select-participation-dialog')"
          >
            Close
          </v-btn>
          <v-btn
          color="blue"
          @click="createParticipation"
        >
          Save
        </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </template>
  
  <script lang="ts">
  import { Vue, Component, Prop, Model } from 'vue-property-decorator';
  import VueCtkDateTimePicker from 'vue-ctk-date-time-picker';
  import 'vue-ctk-date-time-picker/dist/vue-ctk-date-time-picker.css';
  import { ISOtoString } from '@/services/ConvertDateService';
  import Activity from '@/models/activity/Activity';
  import Theme from '@/models/theme/Theme';
  import Enrollment from '@/models/enrollment/Enrollment';
import RemoteServices from '@/services/RemoteServices';
import Participation from '@/models/participation/Participation';


  Vue.component('VueCtkDateTimePicker', VueCtkDateTimePicker);
@Component({
  methods: { ISOtoString },
})

  export default class ParticipantSelectionDialog extends Vue {
    @Model('dialog', Boolean) dialog!: boolean;
    @Prop({ type: Activity, required: true }) readonly activity!: Activity;
    @Prop({ type: Array, required: true }) readonly themes!: Theme[];
    @Prop({ type: Enrollment, required:true}) readonly enrollment!: Enrollment //I know it should be readonly to ensure good parent-child communication but cannot create updateEnrollment service
  

    editActivity: Activity = new Activity(this.activity);
    editEnrollment: Enrollment = new Enrollment(this.enrollment);

    newParticipation: Participation = new Participation();

    //Number id = this.

    rating: string = '';


    async createParticipation() {
      // Method for creating new Participation
      if ((this.$refs.form as Vue & { validate: () => boolean }).validate()) {
        if(this.rating == '' || parseInt(this.rating) > 0 && parseInt(this.rating) <= 5){
          try{
          this.newParticipation.rating = Number(this.rating);
          this.newParticipation.activityId = this.activity.id;
          this.newParticipation.volunteerId = this.enrollment.volunteerId;
          
            const result = await RemoteServices.registerParticipation(this.newParticipation, Number(this.activity.id));

              this.$emit('save-participation', result);
          } catch(error) {
          await this.$store.dispatch('error', error);
        }
        } 
      }
    }
  }
  </script>
  
  <style scoped lang="scss"></style>
  