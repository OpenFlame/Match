/*
 * Copyright 2015 Stuart Scott
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package expression;

import match.IMatch;
import match.ITarget;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Tests for Literals.
 */
public class LiteralTest {

    private static final String FOO = "foo";

    /**
     * Tests that Literals resolve correctly.
     */
    @Test
    public void literal() {
        IMatch match = Mockito.mock(IMatch.class);
        ITarget target = Mockito.mock(ITarget.class);
        IExpression expression = new Literal(match, target, FOO);
        Assert.assertEquals("Wrong resolution", FOO, expression.resolve());
    }

}
