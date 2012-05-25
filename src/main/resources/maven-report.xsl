<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:x="http://www.jenitennison.com/xslt/xspec"
    xmlns:html="http://www.w3.org/1999/xhtml"
    exclude-result-prefixes="xs" version="2.0">
    
    <xsl:param name="projectBaseDir" as="xs:string" select="'C:/intellij_workspace/solinfra/services/search-service/trunk'"/>
    <xsl:param name="resultsDirectoryPath" as="xs:string" select="'file:///C:/Users/johmor/Documents/OxygenXMLEditor/xspec_tests'"/>
    <xsl:param name="reportDirectoryPath" as="xs:string" select="'file:///C:/Users/johmor/Documents/OxygenXMLEditor/xspec_tests/out'"/>
    <xsl:param name="xspecTestDir" as="xs:string" select="'src/test/resources/xspec'"/>
    <xsl:param name="testResultFileList" as="xs:string*" select="'1.xml 2.xml'"/>
    
    <xsl:output encoding="UTF-8" indent="yes" method="html" exclude-result-prefixes="x"/>
    <xsl:output encoding="UTF-8" indent="yes" method="xhtml" name="html" exclude-result-prefixes="x"/>
    
    <xsl:variable name="combinedTests">
        <combined>
            <xsl:for-each select="tokenize($testResultFileList,' ')">
                <xspecTest>
                    <xsl:attribute name="file">
                        <xsl:value-of select="replace(concat($xspecTestDir,'/',replace(.,'-','/')),'.xml$','.xspec')"/>
                    </xsl:attribute>
                    <xsl:copy-of select="doc(concat($resultsDirectoryPath,'/',.))"/>
                </xspecTest>
            </xsl:for-each>
        </combined>
    </xsl:variable>
    
    <xsl:template match="/">
        
        <xsl:message>
            <xsl:text>
            </xsl:text>
            <xsl:value-of select="concat('Parameter projectBaseDir:',$projectBaseDir)"/><xsl:text>
            </xsl:text>
            <xsl:value-of select="concat('Parameter resultsDirectoryPath:',$resultsDirectoryPath)"/><xsl:text>
            </xsl:text>
            <xsl:value-of select="concat('Parameter reportDirectoryPath:',$reportDirectoryPath)"/><xsl:text>
            </xsl:text>
            <xsl:value-of select="concat('Parameter xspecTestDir:',$xspecTestDir)"/><xsl:text>
            </xsl:text>
            <xsl:value-of select="concat('Parameter testResultFileList:',$testResultFileList)"/>
        </xsl:message>
        
        <xsl:variable name="index" exclude-result-prefixes="x">
            
                    <table class="bodyTable">
                        <tr class="a">
                            <th></th> 
                            <th>TestFile</th> 
                            <th>Tests</th> 
                            <th>Pending </th> 
                            <th>Failures</th> 
                            <th>Success Rate</th> 
                            <th>Time</th> 
                        </tr>
                        <xsl:apply-templates select="$combinedTests/combined/xspecTest/x:report"/>
                    </table> 
                
            
        </xsl:variable>
        
        <xsl:copy-of select="$index"/>
        <!--
        <xsl:result-document href="{concat($reportDirectoryPath,'/','index.xml')}">
            <xsl:copy-of select="$index"/>
        </xsl:result-document>
        -->
        
        <xsl:for-each select="tokenize($testResultFileList,' ')">
            <xsl:variable name="html-report-file" select="doc(concat($resultsDirectoryPath,'/',replace(.,'-result.xml$','-report.htm')))"/>
            <xsl:result-document href="{concat($reportDirectoryPath,'/','x',replace(.,'.xml$','.html'))}" method="html">
                <xsl:apply-templates mode="washHtml" select="$html-report-file" exclude-result-prefixes="x"/>
            </xsl:result-document>
        </xsl:for-each>
    
    </xsl:template>
    
    
    <xsl:template match="x:report" exclude-result-prefixes="x">
        <tr class="b">
            <xsl:variable name="totalTests" select="count(.//x:test)"/>
            <xsl:variable name="pendingTests" select="count(.//x:test[@successful = 'pending'])"/>
            <xsl:variable name="failedTests" select="count(.//x:test[@successful = 'false'])"/>
            <xsl:variable name="successfulTests" select="count(.//x:test[@successful = 'true'])"/>
            <xsl:variable name="activeTests" select="$totalTests - $pendingTests"/>
            
            <td>
                <xsl:choose>
                    <xsl:when test="$activeTests = $successfulTests">
                        <img src="images/icon_success_sml.gif" />        
                    </xsl:when>
                    <xsl:otherwise>
                        <img src="images/icon_error_sml.gif" />
                    </xsl:otherwise>
                </xsl:choose>
            </td>
            <td><xsl:value-of select="../@file"/></td> 
            <td><xsl:value-of select="$totalTests"/></td> 
            <td><xsl:value-of select="$pendingTests"/></td> 
            <td><xsl:value-of select="$failedTests"/></td>
            <td>
                <xsl:choose>
                    <xsl:when test="$activeTests > 0 and $successfulTests > 0">
                        <xsl:value-of select="format-number($successfulTests div $activeTests, '##%')"/>    
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="format-number(0,'##%')"/>
                    </xsl:otherwise>
                </xsl:choose>
            </td> 
            <td>not implemented</td> 
        </tr>
    </xsl:template>
    
    
    <!-- Wash HTML Stuff -->
    <xsl:template match="html:link/@href" mode="washHtml">
        <xsl:attribute name="href" select="'../css/xspec-test-report.css'"/>
    </xsl:template>
    
    <xsl:template match="@*|node()" mode="washHtml">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()" mode="washHtml"/>   
        </xsl:copy> 
    </xsl:template>
    
    <xsl:template match="html:head/html:title" mode="washHtml">
        <xsl:analyze-string select="." regex=".*({$projectBaseDir}).*">
            <xsl:matching-substring>
                <title><xsl:value-of select="replace(.,regex-group(1),'')"/></title>
            </xsl:matching-substring>
        </xsl:analyze-string>
    </xsl:template>
    
    <xsl:template match="html:a[contains(@href,$projectBaseDir)]" mode="washHtml" exclude-result-prefixes="x">
        <xsl:analyze-string select="." regex=".*({$projectBaseDir}).*">
            <xsl:matching-substring>
                <xsl:value-of select="replace(.,regex-group(1),'')"></xsl:value-of>
            </xsl:matching-substring>
            <xsl:non-matching-substring><xsl:value-of select="."/></xsl:non-matching-substring>
        </xsl:analyze-string>
    </xsl:template>
    
    
</xsl:stylesheet>
