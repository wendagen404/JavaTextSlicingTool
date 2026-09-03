package com.text_slicing_tool.extractor.impl;

import com.text_slicing_tool.extractor.DocumentExtractor;
import com.text_slicing_tool.pojo.DocumentContent;
import com.text_slicing_tool.pojo.ExtractResult;
import lombok.extern.slf4j.Slf4j;
import org.commonmark.ext.gfm.tables.TableBlock;
import org.commonmark.ext.gfm.tables.TableBody;
import org.commonmark.ext.gfm.tables.TableCell;
import org.commonmark.ext.gfm.tables.TableHead;
import org.commonmark.ext.gfm.tables.TableRow;
import org.commonmark.ext.gfm.tables.TablesExtension;
import org.commonmark.node.AbstractVisitor;
import org.commonmark.node.BulletList;
import org.commonmark.node.Code;
import org.commonmark.node.FencedCodeBlock;
import org.commonmark.node.HardLineBreak;
import org.commonmark.node.Heading;
import org.commonmark.node.HtmlInline;
import org.commonmark.node.ListBlock;
import org.commonmark.node.ListItem;
import org.commonmark.node.Node;
import org.commonmark.node.OrderedList;
import org.commonmark.node.Paragraph;
import org.commonmark.node.SoftLineBreak;
import org.commonmark.node.Text;
import org.commonmark.node.ThematicBreak;
import org.commonmark.parser.Parser;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Markdown 识别。
 */
@Slf4j
public class MarkdownExtractor implements DocumentExtractor {
    @Override
    public ExtractResult extract(ClassPathResource resource) {
        long start = System.currentTimeMillis();
        try (InputStream inputStream = resource.getInputStream()) {
            String markdown = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            Parser parser = Parser.builder()
                    .extensions(List.of(TablesExtension.create()))
                    .build();

            Node document = parser.parse(markdown);
            MarkdownBlockCollector collector = new MarkdownBlockCollector();
            document.accept(collector);

            Map<String, Object> metadata = new LinkedHashMap<>();
            metadata.put("resourcePath", resource.getPath());
            metadata.put("blockCount", collector.blocks.size());
            metadata.put("headingCount", collector.headingCount);
            metadata.put("tableCount", collector.tableCount);

            return ExtractResult.builder()
                    .extractorType("markdown")
                    .durationMillis(System.currentTimeMillis() - start)
                    .metadata(metadata)
                    .content(DocumentContent.builder()
                            .sourceId(resource.getPath())
                            .sourceType("markdown")
                            .text(collector.toText())
                            .metadata(metadata)
                            .build())
                    .build();
        } catch (IOException e) {
            throw new RuntimeException("Markdown识别失败", e);
        }
    }

    private static final class MarkdownBlockCollector extends AbstractVisitor {
        private final List<String> blocks = new ArrayList<>();
        private int headingCount;
        private int tableCount;

        @Override
        public void visit(Heading heading) {
            headingCount++;
            String text = extractPlainText(heading).trim();
            if (!text.isEmpty()) {
                blocks.add("#".repeat(Math.max(1, heading.getLevel())) + " " + text);
            }
        }

        @Override
        public void visit(Paragraph paragraph) {
            String text = extractPlainText(paragraph).trim();
            if (!text.isEmpty()) {
                blocks.add(text);
            }
        }

        @Override
        public void visit(BulletList bulletList) {
            addList(bulletList, "- ");
        }

        @Override
        public void visit(OrderedList orderedList) {
            addList(orderedList, "1. ");
        }

        @Override
        public void visit(FencedCodeBlock fencedCodeBlock) {
            String literal = fencedCodeBlock.getLiteral().trim();
            if (!literal.isEmpty()) {
                blocks.add("```" + safeInfo(fencedCodeBlock.getInfo()) + "\n" + literal + "\n```");
            }
        }

        @Override
        public void visit(ThematicBreak thematicBreak) {
            blocks.add("---");
        }

        public void visit(TableBlock tableBlock) {
            tableCount++;
            String table = extractTable(tableBlock);
            if (!table.isBlank()) {
                blocks.add(table);
            }
        }

        private void addList(ListBlock listBlock, String prefix) {
            List<String> items = new ArrayList<>();
            for (Node item = listBlock.getFirstChild(); item != null; item = item.getNext()) {
                if (item instanceof ListItem listItem) {
                    String text = extractPlainText(listItem).trim();
                    if (!text.isEmpty()) {
                        items.add(prefix + text);
                    }
                }
            }
            if (!items.isEmpty()) {
                blocks.add(String.join("\n", items));
            }
        }

        private String extractTable(TableBlock tableBlock) {
            List<String> rows = new ArrayList<>();
            for (Node child = tableBlock.getFirstChild(); child != null; child = child.getNext()) {
                if (child instanceof TableHead tableHead) {
                    for (Node row = tableHead.getFirstChild(); row != null; row = row.getNext()) {
                        if (row instanceof TableRow tableRow) {
                            rows.add("表头: " + extractTableRow(tableRow));
                        }
                    }
                }
                if (child instanceof TableBody tableBody) {
                    for (Node row = tableBody.getFirstChild(); row != null; row = row.getNext()) {
                        if (row instanceof TableRow tableRow) {
                            rows.add("表格: " + extractTableRow(tableRow));
                        }
                    }
                }
            }
            return String.join("\n", rows);
        }

        private String extractTableRow(TableRow tableRow) {
            List<String> cells = new ArrayList<>();
            for (Node cell = tableRow.getFirstChild(); cell != null; cell = cell.getNext()) {
                if (cell instanceof TableCell tableCell) {
                    cells.add(extractPlainText(tableCell).trim());
                }
            }
            return String.join(" | ", cells);
        }

        private String extractPlainText(Node node) {
            PlainTextCollector collector = new PlainTextCollector();
            node.accept(collector);
            return collector.text.toString();
        }

        private String safeInfo(String info) {
            return info == null ? "" : info.trim();
        }

        private String toText() {
            return String.join("\n\n", blocks);
        }
    }

    private static final class PlainTextCollector extends AbstractVisitor {
        private final StringBuilder text = new StringBuilder();

        @Override
        public void visit(Text textNode) {
            text.append(textNode.getLiteral());
        }

        @Override
        public void visit(SoftLineBreak softLineBreak) {
            text.append(' ');
        }

        @Override
        public void visit(HardLineBreak hardLineBreak) {
            text.append('\n');
        }

        @Override
        public void visit(Code code) {
            text.append(code.getLiteral());
        }

        @Override
        public void visit(HtmlInline htmlInline) {
            text.append(htmlInline.getLiteral());
        }
    }

    public static void main(String[] args) {
        new MarkdownExtractor().extract(new ClassPathResource("document/java.md"));
    }
}
