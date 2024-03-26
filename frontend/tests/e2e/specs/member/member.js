describe('Volunteer', () => {
  beforeEach(() => {
    cy.deleteAllButArs();
    cy.createParticipations();
    cy.demoMemberLogin();
  });

  afterEach(() => {
    cy.logout();
    cy.deleteAllButArs();
  });

  it('close', () => {
    cy.get('[data-cy="institution"]').click();
    cy.get('[data-cy="members"]').click();

    cy.get('[data-cy="institution"]').click();
    cy.get('[data-cy="themes"]').click();

    cy.get('[data-cy="institution"]').click();
    cy.get('[data-cy="activities"]').click();
  });

  it('teste', () => {

    cy.intercept('GET', '/users/*/getInstitution').as('getInstitutions');

    cy.get('[data-cy="institution"]').click();

    cy.get('[data-cy="activities"]').click();
    cy.wait('@getInstitutions');

    //has 2 activities
    cy.get('[data-cy="memberActivitiesTable"] tbody tr')
        .should('have.length', 2)

    //first activity has 1 participation
    cy.get('[data-cy="memberActivitiesTable"] tbody tr')
        .eq(0).children().eq(3).should('contain', 1)

    //first activity has 2 enrollments
    cy.get('[data-cy="memberActivitiesTable"] tbody tr')
        .find('[data-cy="showEnrollments"]')
        .eq(0) // Select the first button
        .click(); // Click on it

    cy.get('[data-cy="activityEnrollmentsTable"] tbody tr')
        .should('have.length', 2)

    //first enrollment of first activity has participating false
    cy.get('[data-cy="activityEnrollmentsTable"] tbody tr')
        .eq(0).children().eq(2).should('contain', false)
  });
});
