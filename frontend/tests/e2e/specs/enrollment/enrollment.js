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
    cy.intercept('GET', '/activities').as('getActivities');

    // visit the activities page
    cy.visit('/activities');

    // wait for activities to load
    cy.wait('@getActivities');

    // verify the table has 3 instances
    cy.get('[data-cy=memberActivitiesTable] tbody tr').should('have.length', 3);

    // verify the first activity has 0 Applications
    cy.get('[data-cy=memberActivitiesTable] tbody tr')
      .first()
      .within(() => {
        cy.get('[data-cy=applications]').should('contain', '0');
      });
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
    cy.intercept('GET', '/activities').as('getActivities');

    // visit the activities page
    cy.visit('/activities');

    // wait for activities to load
    cy.wait('@getActivities');

    // verify the first activity has 1 Application
    cy.get('[data-cy=memberActivitiesTable] tbody tr')
      .first()
      .within(() => {
        cy.get('[data-cy=applications]').should('contain', '1');
      });

    // click showEnrollments
    cy.get('[data-cy="showEnrollments"]').click();

    // Verificar que a tabela de enrollments da atividade tem 1 enrollment com a motivação introduzida
    cy.get('[data-cy="activityEnrollmentsTable"] tbody tr')
      .should('have.length', 1)
      .within(() => {
        cy.get('[data-cy="motivation"]').should('contain', MOTIVATION);
      });

    cy.logout();
  });
});
