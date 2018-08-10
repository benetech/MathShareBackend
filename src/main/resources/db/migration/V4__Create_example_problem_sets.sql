DO $$
DECLARE last_id bigint;
BEGIN
    -- First example data set
    INSERT INTO problem_set DEFAULT VALUES
    RETURNING id INTO last_id;

    INSERT INTO problem_set_revision(problem_set_id) VALUES (last_id)
    RETURNING id INTO last_id;

    INSERT INTO problem(problem_set_revision_id, problem_text, title)
    VALUES (last_id, '3(-\frac{1}{6})(-\frac{2}{5})', 'Find the product');

    INSERT INTO problem(problem_set_revision_id, problem_text, title)
    VALUES (last_id, '-\frac{2}{5}(-\frac{1}{2})(-\frac{5}{6})', 'Find the product');

    INSERT INTO problem(problem_set_revision_id, problem_text, title)
    VALUES (last_id, '\frac{55}{\frac{1}{2}}', 'Find the quotient');

    INSERT INTO problem(problem_set_revision_id, problem_text, title)
    VALUES (last_id, '\frac{3}{10}\div (\frac{5}{8})', 'Find the quotient');

    INSERT INTO problem(problem_set_revision_id, problem_text, title)
    VALUES (last_id, 'Sarah\ works\ at\ a\ coffee\ shop.\ Her\ weekly\ salary\ is\ $325\ and\ she\ earns\ 11.5%\ commission\ on\ sales.\
     How\ much\ does\ she\ make\ if\ she\ sells\ $2800\ in\ merchandise?', 'Answer the question');

    INSERT INTO problem(problem_set_revision_id, problem_text, title)
    VALUES (last_id, '7x-13=1', 'Solve for x');

    INSERT INTO problem(problem_set_revision_id, problem_text, title)
    VALUES (last_id, '\frac{b}{9}-34\leq -36', 'Solve the inequality');

    -- Second example data set
    INSERT INTO problem_set DEFAULT VALUES
    RETURNING id INTO last_id;

    INSERT INTO problem_set_revision(problem_set_id) VALUES (last_id)
    RETURNING id INTO last_id;

    INSERT INTO problem(problem_set_revision_id, problem_text, title)
    VALUES (last_id, '3(-\frac{1}{6})(-\frac{2}{5})', 'Find the product');

    INSERT INTO problem(problem_set_revision_id, problem_text, title)
    VALUES (last_id, '7x-12=2', 'Solve for x');

    INSERT INTO problem(problem_set_revision_id, problem_text, title)
    VALUES (last_id, '3x+14=5', 'Solve for x');

    INSERT INTO problem(problem_set_revision_id, problem_text, title)
    VALUES (last_id, '\frac{b}{9}-34\leq -36', 'Solve the inequality');

    INSERT INTO problem(problem_set_revision_id, problem_text, title)
    VALUES (last_id, 'Angles\ a\ and\ b\ are\ supplementary.\ Angle\ a\ is\ 78\degree\ and\ angle\ b\ is\ equal\ to\ 3x', 'Solve for x');

    INSERT INTO problem(problem_set_revision_id, problem_text, title)
    VALUES (last_id, 'What\ is\ the\ area\ of\ a\ circle\ with\ a\ diameter\ of\ 10\ (use\ \pi\ or\ the\ value\ of\ \pi\ to\ the\ hundredths\ place)?', 'Answer the question');

    INSERT INTO problem(problem_set_revision_id, problem_text, title)
    VALUES (last_id, 'Sarah\ works\ at\ a\ coffee\ shop.\ Her\ weekly\ salary\ is\ $325\ and\ she\ early\ 11.5%\ commission\ on\ sales.\ How\ much\ does\ she\ make\ if\ she\ sells\ $2800\ in\ merchandise?', 'Answer the question');

    -- Third example data set
    INSERT INTO problem_set DEFAULT VALUES
    RETURNING id INTO last_id;

    INSERT INTO problem_set_revision(problem_set_id) VALUES (last_id)
    RETURNING id INTO last_id;

    INSERT INTO problem(problem_set_revision_id, problem_text, title)
    VALUES (last_id, '4(-\frac{2}{9})(-\frac{9}{4})', 'Find the product');

    INSERT INTO problem(problem_set_revision_id, problem_text, title)
    VALUES (last_id, '(-\frac{3}{8})(-\frac{2}{7})(-\frac{4}{6})', 'Find the product');

    INSERT INTO problem(problem_set_revision_id, problem_text, title)
    VALUES (last_id, '(-\frac{4}{3})(\frac{2}{8})(-\frac{6}{2})', 'Find the product');

    INSERT INTO problem(problem_set_revision_id, problem_text, title)
    VALUES (last_id, '5x-10=10', 'Solve for x');

    INSERT INTO problem(problem_set_revision_id, problem_text, title)
    VALUES (last_id, '6x+14=8', 'Solve for x');

    INSERT INTO problem(problem_set_revision_id, problem_text, title)
    VALUES (last_id, '-7y\leq 14', 'Solve for y');

    INSERT INTO problem(problem_set_revision_id, problem_text, title)
    VALUES (last_id, '-3x+13>9', 'Solve for y');
END $$;
