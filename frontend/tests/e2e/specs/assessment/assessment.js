describe('Assessment', () => {
    beforeEach(() => {
        cy.deleteAllButArs();
        cy.createDemoEntities();
        cy.populateAssessments();
        cy.demoVolunteerLogin();
    });

    afterEach(() => {
        cy.logout();
        cy.deleteAllButArs();
    });

    it('volunteer activity table has 6 instances', () => {
        // intercept create assessment request
        //cy.intercept('POST', '/institutions/[0-9]+/assessments').as('saveAssessment');
        // intercept get assessments
        cy.intercept('GET', '/users/getAssessments').as('getAssessments');

        cy.get('[data-cy="volunteerActivities"').click();
        cy.wait('@getAssessments');

        // check results
        cy.get('[data-cy="volunteerActivitiesTable"] tbody tr')
            .should('have.length', 6)
            .eq(0)
            .children()
            .should('have.length', 10)
    });

    it('volunteer activity table first element has name \"A1\"', () => {
        // intercept get assessments
        cy.intercept('GET', '/users/getAssessments').as('getAssessments');

        cy.get('[data-cy="volunteerActivities"').click();
        cy.wait('@getAssessments');

        // check results
        cy.get('[data-cy="volunteerActivitiesTable"] tbody tr')
            .should('have.length', 1)
        cy.get('[data-cy="volunteerActivitiesTable"] tbody tr')
            .eq(0).children().eq(0).should('contain', 'A1')
    });
});
