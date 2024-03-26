describe("Assessment", () => {
    beforeEach( () => {
        cy.deleteAllButArs();
        cy.hedb_create_assessments();
    });

    afterEach( () => {
        cy.deleteAllButArs();
    });

    it('create assessments', () => {
        const REVIEW = "This is a review";

        cy.demoMemberlogin();

        cy.intercept('GET', '/institutions/*/assessments').as('getAssessments');
        // go to institution assessments page
        cy.get('[data-cy="institution"]').click();
        cy.get('[data-cy="assessments"]').click();
        cy.wait('@getAssessments');
        // check if table has only one assessment (and is formatted correctly)
        cy.get('[data-cy="institutionAssessmentsTable"] tbody tr')
            .should('have.length', 1)
            .eq(0)
            .children()
            .should('have.length', 3);
        // check if review has the text inserted by the volunteer
        cy.get('[data-cy="institutionAssessmentTable"] tbody tr')
            .eq(0)
            .children()
            .eq(0).should('contain', REVIEW);
        // logout
        cy.logout();
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

    it('assessment creation success', () => {
        const REVIEW = "This is a test review.";
        const VOLUNTEER_NAME = "DEMO_VOLUNTEER";
        let REVIEW_DATE;
        // intercept create assessment request
        cy.intercept('POST', '/institutions/[0-9]+/assessments', (req) => {
            REVIEW_DATE = req.body.reviewDate;
        }).as('saveAssessment');
        // intercept get assessments
        cy.intercept('GET', '/users/getAssessments').as('getAssessments');

        cy.get('[data-cy="volunteerActivities"').click();
        cy.wait('@getAssessments');

        cy.get('data-cy="writeAssessmentButton"').eq(0).click();

        cy.get('data-cy="reviewInput"').type(REVIEW);

        cy.get('data-cy="saveAssessment"').click();

        cy.wait('@saveAssessment');

        cy.logout();
        cy.demoMemberLogin();

        cy.get('[data-cy="institution"]').click();

        cy.get('[data-cy="assessments"]').click();

        cy.wait('@getAssessments');

        cy.get('[data-cy="memberAssessmentsTable"] tbody tr')
            .should('have.length', 1)
            .eq(0)
            .children()
            .should('have.length', 3)
        cy.get('[data-cy="memberActivitiesTable"] tbody tr')
            .eq(0).children().eq(0).should('contain', REVIEW)
        cy.get('[data-cy="memberActivitiesTable"] tbody tr')
            .eq(0).children().eq(1).should('contain', REVIEW_DATE)
        cy.get('[data-cy="memberActivitiesTable"] tbody tr')
            .eq(0).children().eq(2).should('contain', VOLUNTEER_NAME)
    });
});