describe('Volunteer', () => {
  beforeEach(() => {
    cy.deleteAllButArs();
    cy.createDemoEntities();
    cy.createEnrollmentTestEntities();
  });

  afterEach(() => {
    cy.deleteAllButArs();
  });

  it('create enrollment', () => {
    const MOTIVATION = 'Enrollment Motivation';

    // demo login as member
    cy.demoMemberLogin();
    // intercept requests
    cy.intercept('GET', '/activities/*/enrollments').as(
      'getActivityEnrollments',
    );
    // visit the activities page
    cy.get('[data-cy="institution"]').click(); // Click on the "Institution" button to open the menu
    cy.get('[data-cy="activities"]').click();
    // verify the table has 3 instances
    cy.get('[data-cy=memberActivitiesTable] tbody tr').should('have.length', 3);
    // verify the first activity has 0 Applications
    cy.get('[data-cy="memberActivitiesTable"] tbody tr')
      .eq(0)
      .children()
      .eq(11)
      .should('have.text', 0);
    cy.get('[data-cy="showEnrollments"]').eq(0).click();
    //checking if the activity enrollments table is empty
    cy.get('[data-cy="activityEnrollmentsTable"] tbody')
      .find('.v-data-table__empty-wrapper')
      .should('exist');
    cy.logout();

    // demo login as volunteer
    cy.demoVolunteerLogin();
    cy.intercept('GET', '/activities').as('getActivities');
    cy.intercept('GET', '/enrollments/volunteer').as('getVolunteerEnrollments');
    // go to volunteer activities page
    cy.get('[data-cy="volunteerActivities"]').click();
    cy.wait('@getActivities');
    cy.wait('@getVolunteerEnrollments');
    // click on the apply button
    cy.get('[data-cy="enrollButton"]').click();
    // fill the motivaiton
    cy.get('[data-cy="motivationInput"]').type(MOTIVATION);
    cy.get('[data-cy="saveEnrollment"]').click();
    //check if there is no more apply buttons
    cy.get('[data-cy="enrollButton"]').should('not.exist');
    cy.logout();

    // demo login as member
    cy.demoMemberLogin();
    // intercept requests
    cy.intercept('GET', '/activities/*/enrollments').as(
      'getActivityEnrollments',
    );
    // visit the activities page
    cy.get('[data-cy="institution"]').click(); // Click on the "Institution" button to open the menu
    cy.get('[data-cy="activities"]').click();
    // verify the first activity has 1 Application
    cy.get('[data-cy="memberActivitiesTable"] tbody tr')
      .eq(0)
      .children()
      .eq(10)
      .should('have.text', 1);
    // click showEnrollments
    cy.get('[data-cy="showEnrollments"]').eq(0).click();
    cy.wait('@getActivityEnrollments');
    // Verificar que a tabela de enrollments da atividade tem 1 enrollment com a motivação introduzida
    cy.get('[data-cy="activityEnrollmentsTable"] tbody tr').should(
      'have.length',
      1,
    );
    cy.get('[data-cy="activityEnrollmentsTable"] tbody tr')
      .eq(0)
      .children()
      .eq(0)
      .should('contain', MOTIVATION);

    cy.logout();
  });
});
