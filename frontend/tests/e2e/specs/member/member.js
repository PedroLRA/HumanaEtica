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
});
