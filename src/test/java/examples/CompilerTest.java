package examples;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import com.google.javascript.jscomp.CompilationLevel;
import com.google.javascript.jscomp.Compiler;
import com.google.javascript.jscomp.CompilerOptions;
import com.google.javascript.jscomp.Result;
import com.google.javascript.jscomp.SourceFile;
import com.pholser.junit.quickcheck.From;
import edu.berkeley.cs.jqf.fuzz.Fuzz;
import edu.berkeley.cs.jqf.fuzz.JQF;
import org.junit.Before;
import org.junit.runner.RunWith;

import static org.junit.Assume.*;

@RunWith(JQF.class)
public class CompilerTest {

    static {
        // Disable all logging by Closure passes, to speed up fuzzing
        java.util.logging.LogManager.getLogManager().reset();
    }

    // Compiler, options, and predefined JS environment
    private Compiler compiler = new Compiler(new PrintStream(new ByteArrayOutputStream(), false));
    private CompilerOptions options = new CompilerOptions();
    private SourceFile externs = SourceFile.fromCode("externs", "");

    @Before // Runs before tests are executed
    public void initCompiler() {
        // Don't use threads
        compiler.disableThreads();
        // Don't print things
        options.setPrintConfig(false);
        // Enable all safe optimizations
        CompilationLevel.SIMPLE_OPTIMIZATIONS.setOptionsForCompilationLevel(options);
    }

    /** Compiles an input and returns its result */
    private Result compile(SourceFile input) {
        Result result = compiler.compile(externs, input, options);
        assumeTrue(result.success); // Semantic validity check
        return result;
    }

    /** Entry point for fuzzing with default (arbitrary) string generator */
    @Fuzz
    public void testWithGenerator(@From(JavaScriptCodeGenerator.class) String code) {
        SourceFile input = SourceFile.fromCode("input", code);
        compile(input);
    }
}