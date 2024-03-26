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

  });
});
