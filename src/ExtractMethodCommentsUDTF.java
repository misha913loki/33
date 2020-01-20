import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.expr.LiteralExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;

import io.netty.util.internal.StringUtil;


@Resolve("bigint,string,string->bigint,string,string,string,string")
public class ExtractMethodCommentsUDTF extends UDTF{
    public static final Logger log = LoggerFactory.getLogger(ExtractMethodCommentsUDTF.class);

    @Override
    public void process(Object[] args) throws UDFException, IOException {
        // project_id, file_path, file_content
        Long projectId = (Long)args[0];
        String filePath = (String)args[1];
        String fileContent = (String)args[2];
        if (projectId == null || filePath == null || fileContent == null) {
            return;
        }
        if (CodeLineUtil.separateLine(fileContent).length > MAX_LINE_COUNT) {
            return;
        }
        log.info("Processing projectId={} filePath={}", projectId, filePath);
        CompilationUnit cu;
        
        try {
            cu = JavaParser.parse(fileContent);
            List<MethodDeclaration> methods = cu.findAll(MethodDeclaration.class);
            for (MethodDeclaration method: methods) {
                processMethod(projectId, filePath, method);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    
    }

}
