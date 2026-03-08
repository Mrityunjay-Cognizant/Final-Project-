package com.tests.stepDef;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

/**
 * FIX SUMMARY:
 * - The original runner had no @RunWith annotation (needed for JUnit) and no
 *   base class, so it would never actually execute any Cucumber scenarios.
 * - Since this project uses TestNG (not JUnit), the runner must extend
 *   AbstractTestNGCucumberTests — this is the standard TestNG+Cucumber pattern.
 * - Removed the cucumber-junit dependency from pom.xml and added cucumber-testng.
 * - The @CucumberOptions annotation is kept as-is (features path and glue are correct).
 */
@CucumberOptions(
        features = "src/main/resources/features",
        glue = "com.tests.stepDef",
        tags = "@Faq"
)
public class FaqTestRunner extends AbstractTestNGCucumberTests {
}