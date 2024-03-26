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
    const MOTIVATION = "Enrollment Motivation";

    // demo login as volunteer
    cy.demoVolunteerLogin();
    cy.intercept('GET', '/activities').as('getActivities');
    cy.intercept('GET', '/enrollments/volunteer').as('getVolunteerEnrollments');
    // go to volunteer activities page
    cy.get('[data-cy="volunteerActivities"]').click();
    cy.wait('@getActivities')
    cy.wait('@getVolunteerEnrollments')
    // click on the apply button
    cy.get('[data-cy="enrollButton"]').click();
    // fill the motivaiton
    cy.get('[data-cy="motivationInput"]').type(MOTIVATION);
    cy.get('[data-cy="saveEnrollment"]').click();
    //check if there is no more apply buttons
    cy.get('[data-cy="enrollButton"]').should("not.exist");
    cy.logout();
  });
});
