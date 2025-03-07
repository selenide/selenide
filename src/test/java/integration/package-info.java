/**
 * Integration tests for Selenide.
 * <p>
 * These tests require a real browser and run quite slowly.
 * We typically run them in all basic browsers (firefox, chrome).
 * <p>
 * They can execute from command line: "./gradle allTests"
 * <p>
 * They can also be executed for concrete browsers:
 * "./gradle firefox"
 * "./gradle chrome"
 * "./gradle ie"
 * "./gradle opera"
 */
@NullMarked
@CheckReturnValue
package integration;

import com.google.errorprone.annotations.CheckReturnValue;
import org.jspecify.annotations.NullMarked;
