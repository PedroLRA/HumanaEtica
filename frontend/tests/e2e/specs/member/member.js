describe('Volunteer', () => {
  beforeEach(() => {
    cy.demoMemberLogin();
    cy.deleteAllButArs();
    cy.createParticipations();
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

  it('test', () => {
    cy.intercept('GET', '/users/*/getInstitution').as('getInstitutions');
    cy.get('[data-cy="institution"]').click();
    cy.get('[data-cy="activities"]').click();
    cy.wait('@getInstitutions');
    cy.get('[data-cy="memberActivitiesTable"]')
        .should('have.length', 2)
  });
});
