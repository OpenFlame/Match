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
package expression.function;

import config.Config;
import expression.Expression;
import expression.ExpressionList;
import expression.IExpression;
import expression.Literal;
import match.IMatch;
import match.ITarget;
import match.MatchTest;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;

public class ZipTest {

    private static final String FOOBAR = "FooBar";
    private static final String FOO = "Foo";
    private static final String FOO_JAR = "out/java/jar/FooBar.jar";
    private static final String C_DIR = "c/";
    private static final String C_D_DIR = "c/d";
    private static final String C_D_E_FILE = "c/d/e";
    private static final String C_D_F_FILE = "c/d/f";
    private static final String ZIPS_OUT = "out/zip/";
    private static final String ZIP_OUT = "out/zip/FooBar.zip";
    private static final String SOURCES = C_D_E_FILE + " " + C_D_F_FILE + " " + FOO_JAR;
    private static final String MKDIR_ZIP_OUT_COMMAND = String.format("mkdir -p %s", ZIPS_OUT);

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();
    public Config config;
    public File root;
    public String rootPath;
    public File mCDir;
    public File mCDEFile;
    public File mCDFFile;

    @Before
    public void setUp() throws IOException {
        root = folder.getRoot();
        MatchTest.createFileStructure(root);
        config = new Config();
        config.put("root", root.toPath().toString());
        mCDir = new File(root, C_DIR);
        mCDEFile = new File(root, C_D_E_FILE);
        mCDFFile = new File(root, C_D_F_FILE);
        /*
        List<String> files = new ArrayList();
        Find.scanFiles(mRoot, "", files, Pattern.compile(".*"));
        Assert.assertEquals("Dir Structure Not Sound", C_D_E_FILE + " " + C_D_F_FILE, files.toString());
        */
    }

    @After
    public void tearDown() throws IOException {
        //
    }

    @Test
    public void zip() {
        IMatch match = Mockito.mock(IMatch.class);
        ITarget target = Mockito.mock(ITarget.class);
        Mockito.when(match.getRootDir()).thenReturn(root);
        Mockito.when(match.getProperty(FOO)).thenReturn(FOO_JAR);
        Mockito.when(target.getDirectory()).thenReturn(root);
        Mockito.when(target.getFile()).thenReturn(new File(root, "match"));
        Map<String, IExpression> parameters = new HashMap<>();
        parameters.put(Function.NAME, new Literal(match, target, FOOBAR));
        List<IExpression> elements = new ArrayList<IExpression>();
        GetFile g = new GetFile(match, target, Collections.singletonMap(Function.ANONYMOUS, new Literal(match, target, FOO)));
        g.configure();
        elements.add(g);
        Find f = new Find(match, target, Collections.singletonMap(Function.ANONYMOUS, new Literal(match, target, C_D_DIR)));
        f.configure();
        elements.add(f);
        ExpressionList sources = new ExpressionList(match, target, elements);
        parameters.put(Function.SOURCE, sources);
        IFunction function = new Zip(match, target, parameters);
        function.configure();
        String output = new File(root, ZIP_OUT).toPath().toString();
        Assert.assertEquals("Wrong resolution", output, function.resolve());

        Mockito.verify(match, Mockito.times(1)).setProperty(Mockito.eq(FOOBAR), Mockito.eq(output));
        Mockito.verify(match, Mockito.times(1)).addFile(Mockito.eq(output));
        Mockito.verify(match, Mockito.times(1)).awaitFile(Mockito.eq(FOO_JAR));
        Mockito.verify(match, Mockito.times(1)).awaitFile(Mockito.eq(mCDEFile.toPath().normalize().toAbsolutePath().toString()));
        Mockito.verify(match, Mockito.times(1)).awaitFile(Mockito.eq(mCDFFile.toPath().normalize().toAbsolutePath().toString()));
        Mockito.verify(target, Mockito.times(1)).runCommand(Mockito.eq(MKDIR_ZIP_OUT_COMMAND));
        Mockito.verify(target, Mockito.times(1)).runCommand(Mockito.eq(String.format("zip -r %s %s", output, SOURCES)));
    }

}
