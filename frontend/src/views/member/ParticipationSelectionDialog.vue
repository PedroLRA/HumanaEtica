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


  Vue.component('VueCtkDateTimePicker', VueCtkDateTimePicker);
@Component({
  methods: { ISOtoString },
})

  export default class ParticipantSelectionDialog extends Vue {
    @Model('dialog', Boolean) dialog!: boolean;
    @Prop({ type: Activity, required: true }) readonly activity!: Activity;
    @Prop({ type: Array, required: true }) readonly themes!: Theme[];
    @Prop({ type: Enrollment, required:true}) readonly enrollment!: Enrollment
  
    rating: string = '';
  
    async updateActivity() {
      // Method for updating activity or selecting participant
      // Implement your logic here
    }
  }
  </script>
  
  <style scoped lang="scss"></style>
  