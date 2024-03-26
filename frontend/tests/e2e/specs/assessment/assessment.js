describe("Assessment", () => {
    beforeEach( () => {
        cy.deleteAllButArs();
        cy.hedb-create-assessments();
    });

    afterEach( () => {
        cy.deleteAllButArs();
    });

});