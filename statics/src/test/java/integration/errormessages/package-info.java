/*
 * Tests for getting element / collection in different way
 * if element / collection is not found or does not exist
 * exception and its message should be appropriate and enough verbose to understand cause of error
 *
 * tests
 *      collection
 *                get entity
 *                          $$
 *                          $$.filter
 *                          $.findAll
 *                actions
 *                       with wait
 *                                should(condition)
 *                       without wait
 *                                   getTexts()
 *                                   size()
 *                                   isEmpty()
 *      element
 *             get entity
 *                       $
 *                       $$.get(index)
 *                       $$.findBy(condition)
 *                       $.find
 *                       more complicated & useful enough
 *                                                       $$.filterBy(condition).findBy(condition).find
 *                                                       $$.filterBy(condition).get(index).find
 *             actions
 *                    with wait
 *                             should(condition)
 *                             visibility
 *                                       click()
 *                                       doubleclick()
 *                                       ...
 *                             existence in DOM
 *                                             getValue()
 *                                             text()
 *                                             innerText()
 *                                             ...
 *                    without waiting
 *                                   isDisplayed()
 *                                   exist()
 *
 *  fail options
 *             fail on getting element / elements
 *             nonexistent WebElement / WebElements
 *             invalid locator
 *             IndexOutOfRange
 *             not satisfied condition
 *
 * fail on should (todo - after a decision about correct error & caused by error - asserts fix needed)
 *                (comment at MethodCalledOnCollectionFailsOnTest:
 *               todo - hypothesis - error should be according to condition plus caused by ElementNotFound )
 *
 * additional tests (todo - one test per each option with condition usage)
 * conditions rendering
 *                     not(condition)
 *                     and(name, conditions)
 *                     or(name, conditions)
 *
 */

package integration.errormessages;
