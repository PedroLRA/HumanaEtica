<template>
  <div>
    <v-card class="table">
      <v-data-table
        :headers="headers"
        :items="activities"
        :search="search"
        disable-pagination
        :hide-default-footer="true"
        :mobile-breakpoint="0"
        data-cy="volunteerActivitiesTable"
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
          </v-card-title>
        </template>
        <template v-slot:[`item.themes`]="{ item }">
          <v-chip v-for="theme in item.themes" v-bind:key="theme.id">
            {{ theme.completeName }}
          </v-chip>
        </template>
        <template v-slot:[`item.action`]="{ item }">
          <v-tooltip v-if="item.state === 'APPROVED'" bottom>
            <template v-slot:activator="{ on }">
              <v-icon
                class="mr-2 action-button"
                color="red"
                v-on="on"
                data-cy="reportButton"
                @click="reportActivity(item)"
                >warning</v-icon
              >
            </template>
            <span>Report Activity</span>
          </v-tooltip>
          <v-tooltip v-if="canApply(item)" bottom>
            <template v-slot:activator="{ on }">
              <v-icon
                class="mr-2 action-button"
                color="blue"
                v-on="on"
                data-cy="enrollButton"
                @click="newEnrollment(item)"
                >mdi-arrow-right-bold-circle</v-icon
              >
            </template>
            <span>Apply for Activity</span>
          </v-tooltip>
        </template>
      </v-data-table>
      <enrollment-dialog
        v-if="currentEnrollment && createEnrollmentDialog"
        v-model="createEnrollmentDialog"
        :enrollment="currentEnrollment"
        v-on:save-enrollment="onSaveEnrollment"
        v-on:close-enrollment-dialog="onCloseEnrollmentDialog"
      />
    </v-card>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import Activity from '@/models/activity/Activity';
import Enrollment from '@/models/enrollment/Enrollment';
import EnrollmentDialog from './EnrollmentDialog.vue';

@Component({
  components: {
    'enrollment-dialog': EnrollmentDialog,
  },
})
export default class VolunteerActivitiesView extends Vue {
  activities: Activity[] = [];
  userEnrollments: Enrollment[] = [];
  search: string = '';

  currentEnrollment: Enrollment | null = null;
  createEnrollmentDialog: boolean = false;

  headers: object = [
    {
      text: 'Name',
      value: 'name',
      align: 'left',
      width: '5%',
    },
    {
      text: 'Region',
      value: 'region',
      align: 'left',
      width: '5%',
    },
    {
      text: 'Participants',
      value: 'participantsNumberLimit',
      align: 'left',
      width: '5%',
    },
    {
      text: 'Themes',
      value: 'themes',
      align: 'left',
      width: '5%',
    },
    {
      text: 'Description',
      value: 'description',
      align: 'left',
      width: '30%',
    },
    {
      text: 'State',
      value: 'state',
      align: 'left',
      width: '5%',
    },
    {
      text: 'Start Date',
      value: 'formattedStartingDate',
      align: 'left',
      width: '5%',
    },
    {
      text: 'End Date',
      value: 'formattedEndingDate',
      align: 'left',
      width: '5%',
    },
    {
      text: 'Application Deadline',
      value: 'formattedApplicationDeadline',
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
    await this.$store.dispatch('loading');
    try {
      this.activities = await RemoteServices.getActivities();
      this.userEnrollments = await RemoteServices.getVolunteerEnrollments();
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  async reportActivity(activity: Activity) {
    if (activity.id !== null) {
      try {
        const result = await RemoteServices.reportActivity(
          this.$store.getters.getUser.id,
          activity.id,
        );
        this.activities = this.activities.filter((a) => a.id !== activity.id);
        this.activities.unshift(result);
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
  }

  //Conditional rendering to Apply button
  canApply(activity: Activity): boolean {
    const isEnrolled = this.isAlreadyEnrolled(activity);
    const isApplicationOpen = this.isApplicationOpen(activity);
    return !isEnrolled && isApplicationOpen;
  }

  isAlreadyEnrolled(activity: Activity) {
    if (this.userEnrollments.length > 0) {
      const enrolled = this.userEnrollments.filter(
        (a: Enrollment) => a.activityId === activity.id,
      );
      if (enrolled.length > 0) {
        return true;
      }
      return false;
    }
    return false;
  }

  isApplicationOpen(activity: Activity): boolean {
    const now = new Date();
    const applicationDeadline = new Date(activity.applicationDeadline);
    return applicationDeadline > now;
  }

  newEnrollment(activity: Activity) {
    if (activity.id != null) {
      this.currentEnrollment = new Enrollment();
      this.currentEnrollment.activityId = activity.id;
      this.createEnrollmentDialog = true;
    }
  }

  onCloseEnrollmentDialog() {
    this.currentEnrollment = null;
    this.createEnrollmentDialog = false;
  }

  async onSaveEnrollment() {
    console.log('save enrollment');
  }
}
</script>

<style lang="scss" scoped></style>
