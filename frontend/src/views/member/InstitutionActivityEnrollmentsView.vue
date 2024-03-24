<template>
  <v-card class="table">
    <div class="text-h3">{{ activity.name }}</div>
    <v-data-table
      :headers="headers"
      :items="enrollments"
      :search="search"
      disable-pagination
      :hide-default-footer="true"
      :mobile-breakpoint="0"
      data-cy="activityEnrollmentsTable"
    >
      <template v-slot:top>
        <v-card-title>
          <v-text-field
            v-model="search"
            append-icon="search"
            label="Search"
            class="mx-2"
          />
          <v-spacer />
          <v-btn
            color="primary"
            dark
            @click="getActivities"
            data-cy="getActivities"
            >Activities</v-btn
          >
        </v-card-title>
      </template>
      <template v-slot:[`item.themes`]="{ item }">
        <v-chip v-for="theme in item.themes" v-bind:key="theme.id">
          {{ theme.completeName }}
        </v-chip>
      </template>
      <template v-slot:[`item.action`] = "{ item }" >
          <v-tooltip v-if="canParticipate(item)" bottom>
            <template v-slot:activator="{ on }">
              <v-icon
                class="mr-2 action-button"
                color="grey"
                v-on="on"
                data-cy="selectParticipantButton"
                @click="selectNewParticipant()"
              >mdi-checkbox-marked</v-icon>
            </template>
            <span>Select Participant</span>
          </v-tooltip>
      </template>
    </v-data-table>
  </v-card>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import Activity from '@/models/activity/Activity';
import Enrollment from '@/models/enrollment/Enrollment';

@Component({})
export default class InstitutionActivityEnrollmentsView extends Vue {
  activity!: Activity;
  enrollments: Enrollment[] = [];
  search: string = '';

  headers: object = [
    {
      text: 'Volunteer Name',
      value: 'volunteerName',
      align: 'left',
      width: '30%',
    },
    {
      text: 'Motivation',
      value: 'motivation',
      align: 'left',
      width: '50%',
    },
    {
      text: 'Participating',
      value: 'participating',
      align: 'left',
      width: '5%',
    },
    {
      text: 'Application Date',
      value: 'enrollmentDateTime',
      align: 'left',
      width: '5%',
    },
    {
      text: 'Actions',
      value: 'action',
      align: 'left',
      sortable: false,
      width: '5%',
    },
  ];

  async created() {
    this.activity = this.$store.getters.getActivity;
    if (this.activity !== null && this.activity.id !== null) {
      await this.$store.dispatch('loading');
      try {
        this.enrollments = await RemoteServices.getActivityEnrollments(
          this.activity.id,
        );

      } catch (error) {
        await this.$store.dispatch('error', error);
      }
      await this.$store.dispatch('clearLoading');
    }
  }

  async getActivities() {
    await this.$store.dispatch('setActivity', null);
    this.$router.push({ name: 'institution-activities' }).catch(() => {});
  }

    //Conditional rendering to Apply button
    canParticipate(enrollment:Enrollment): boolean {
      //const isParticipating = this.isVolunteerParticipating(activity);
      const isAnySpotOpen = this.isApplicationOpen();
      return isAnySpotOpen;
  }

  /*isVolunteerParticipating(activity: Activity) {
    if (this.enrollments.) {
      
    }

      if (enrolled.length > 0) {
        return true;
      }
      return false;
    
    return false;
  }*/

  isApplicationOpen() {
    if(this.activity.numberOfParticipations < this.activity.participantsNumberLimit){
      return true;
    }  
    return false;
  }


  selectNewParticipant() {
    //Call dialog and other function to create enrollment
    console.log('click');
  }



}
</script>

<style lang="scss" scoped>
.date-fields-container {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
}

.date-fields-row {
  display: flex;
  gap: 16px;
  margin-top: 8px;
}
</style>