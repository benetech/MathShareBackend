DO $$
DECLARE
    last_id bigint;
BEGIN
    -- First example data set
    INSERT INTO problem_set DEFAULT VALUES
    RETURNING id INTO last_id;

    UPDATE problem_set SET title='Example Problem Set' WHERE id=last_id;

    INSERT INTO problem_set_revision(problem_set_id, is_example) VALUES (last_id, true)
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
    VALUES (last_id, 'Sarah\ works\ at\ a\ coffee\ shop.\ Her\ weekly\ salary\ is\ $325\ and\ she\ earns\ 11.5%\ commission\ on\ sales.\How\ much\ does\ she\ make\ if\ she\ sells\ $2800\ in\ merchandise?', 'Answer the question');

    INSERT INTO problem(problem_set_revision_id, problem_text, title)
    VALUES (last_id, '7x-13=1', 'Solve for x');

    INSERT INTO problem(problem_set_revision_id, problem_text, title)
    VALUES (last_id, '\frac{b}{9}-34\leq -36', 'Solve the inequality');

    -- Second example data set
    INSERT INTO problem_set DEFAULT VALUES
    RETURNING id INTO last_id;

    UPDATE problem_set SET title='Solve for X' WHERE id=last_id;

    INSERT INTO problem_set_revision(problem_set_id, is_example) VALUES (last_id, true)
    RETURNING id INTO last_id;

    INSERT INTO problem(problem_set_revision_id, problem_text, title)
    VALUES (last_id, '\frac{x}{6}=\frac{3x}{4}', 'Solve for X');

    INSERT INTO problem(problem_set_revision_id, problem_text, title)
    VALUES (last_id, '7x=3.25', 'Predict whether the solution will be positive, negative, or zero and then solve for x');

    INSERT INTO problem(problem_set_revision_id, problem_text, title)
    VALUES (last_id, '7x=32.5', 'Predict whether the solution will be positive, negative, or zero and then solve for x');

    INSERT INTO problem(problem_set_revision_id, problem_text, title)
    VALUES (last_id, '3x+11=11', 'Predict whether the solution will be positive, negative, or zero and then solve for x');

    INSERT INTO problem(problem_set_revision_id, problem_text, title)
    VALUES (last_id, '9-4x=4', 'Predict whether the solution will be positive, negative, or zero and then solve for x');

    INSERT INTO problem(problem_set_revision_id, problem_text, title)
    VALUES (last_id, '-8+5x=-20', 'Predict whether the solution will be positive, negative, or zero and then solve for x');

    INSERT INTO problem(problem_set_revision_id, problem_text, title)
    VALUES (last_id, '\frac{1}{2}\left(-8+5x\right)=-20', 'Predict whether the solution will be positive, negative, or zero and then solve for x');

    -- Third example data set
    INSERT INTO problem_set DEFAULT VALUES
    RETURNING id INTO last_id;

    UPDATE problem_set SET title='Distance Rate and Time' WHERE id=last_id;

    INSERT INTO problem_set_revision(problem_set_id, is_example) VALUES (last_id, true)
    RETURNING id INTO last_id;

    INSERT INTO problem(problem_set_revision_id, problem_text, title)
    VALUES (last_id, '120\text{ miles/hour, }3\text{ hours, solve for }x\text{ (distance)}', 'An airplane moves at a constant speed of 120 miles per hour for 3 hours. How far does it go?');

    INSERT INTO problem(problem_set_revision_id, problem_text, title)
    VALUES (last_id, '\frac{6\text{ miles}}{4\text{ minutes}}=x', 'A train moves at constant speed and travels 6 miles in 4 minutes. What is its speed in miles per minute?=');

    INSERT INTO problem(problem_set_revision_id, problem_text, title)
    VALUES (last_id, '\text{solve for x}', 'A car moves at a constant speed of 50 miles per hour. How long does it take the car to go 200 miles?');


    -- Fourth example data set
    INSERT INTO problem_set DEFAULT VALUES
    RETURNING id INTO last_id;

    UPDATE problem_set SET title='Combining Like Terms' WHERE id=last_id;

    INSERT INTO problem_set_revision(problem_set_id, is_example) VALUES (last_id, true)
    RETURNING id INTO last_id;

    INSERT INTO problem(problem_set_revision_id, problem_text, title)
    VALUES (last_id, '5+2+3=5+\left(2+3\right)', 'Explain why this statement is true');

    INSERT INTO problem(problem_set_revision_id, problem_text, title)
    VALUES (last_id, '9a\text{ is equivalent to }11a-2a', 'Explain why this statement is true');

    INSERT INTO problem(problem_set_revision_id, problem_text, title)
    VALUES (last_id, '7a+4-2a\text{ is equivalent to }11a-2a', 'Explain why this statement is true');

    INSERT INTO problem(problem_set_revision_id, problem_text, title)
    VALUES (last_id, '8a-\left(8a-8\right)\text{ is equivalent to }8', 'Explain why this statement is true');

    INSERT INTO problem(problem_set_revision_id, problem_text, title)
    VALUES (last_id, '7a+5b-3a+4b', '"Diego and Jada are both trying to write an expression with fewer terms that is equivalent to. Jada thinks 10a+1b is equivalent to the original expression. Diego thinks 4a+9b is equivalent to the original expression.  Who is correct and show why.');

    INSERT INTO problem(problem_set_revision_id, problem_text, title)
    VALUES (last_id, '6x+?=10x', 'Replace each ? with an expression that will make the left side of the equation equivalent to the right side.');

    INSERT INTO problem(problem_set_revision_id, problem_text, title)
    VALUES (last_id, '6x+?=2x', 'Replace each ? with an expression that will make the left side of the equation equivalent to the right side.');

    INSERT INTO problem(problem_set_revision_id, problem_text, title)
    VALUES (last_id, '6x-?=4x-10', 'Replace each ? with an expression that will make the left side of the equation equivalent to the right side.');


    -- Fifth example data set
    INSERT INTO problem_set DEFAULT VALUES
    RETURNING id INTO last_id;

    UPDATE problem_set SET title='Using equations to solve problems' WHERE id=last_id;

    INSERT INTO problem_set_revision(problem_set_id, is_example) VALUES (last_id, true)
    RETURNING id INTO last_id;

    INSERT INTO problem(problem_set_revision_id, problem_text, title)
    VALUES (last_id, '\text{Solve each problem}', 'A performer expects to sell 5,000 tickets for an upcoming concert. They want to make a total of $311,000 in sales from these tickets.');

    INSERT INTO problem(problem_set_revision_id, problem_text, title)
    VALUES (last_id, '\text{solve}', 'Assuming that all tickets have the same price, what is the price for one ticket?');

    INSERT INTO problem(problem_set_revision_id, problem_text, title)
    VALUES (last_id, '\text{solve}', 'How much will they make if they sell 7,000 tickets?');

    INSERT INTO problem(problem_set_revision_id, problem_text, title)
    VALUES (last_id, '\text{solve}', 'How much will they make if they sell 10,000 tickets? 50,000? 120,000? a million? x tickets?');

    INSERT INTO problem(problem_set_revision_id, problem_text, title)
    VALUES (last_id, '\text{solve}', 'If they make $379,420, how many tickets have they sold?');

    INSERT INTO problem(problem_set_revision_id, problem_text, title)
    VALUES (last_id, '\text{solve}', 'How many tickets will they have to sell to make $5,000,000?');
END $$;
