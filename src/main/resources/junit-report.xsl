<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet exclude-result-prefixes="#all"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:x="http://www.jenitennison.com/xslt/xspec"
                xmlns:functx="http://www.functx.com"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                version="2.0">
	
	<xsl:output indent="yes"/>
	<xsl:template match="/">
		<testsuite>
			<xsl:variable name="styleSheet" select="substring(/x:report/@stylesheet, functx:index-of-string-last(/x:report/@stylesheet, '/')+1)"/>
			
			<xsl:attribute name="errors" select="'0'"/>
			<xsl:attribute name="failures" select="count(//x:test[@successful='false'])"/>
			<xsl:attribute name="name" select="$styleSheet"/>
			<xsl:attribute name="timestamp" select="/x:report/@date"/>
		    <xsl:attribute name="tests" select="count(//x:test)"/>	
			<xsl:attribute name="time" select="0.1"/>			
			
			<xsl:apply-templates select="//x:test"/>
		</testsuite>
	</xsl:template>
	
	<xsl:template match="x:test">
		<testcase>
			<xsl:attribute name="classname">
				<xsl:value-of select="string-join(./ancestor::x:scenario/x:label,'/')"/>	
			</xsl:attribute>
			<xsl:attribute name="name">
				<xsl:value-of select="x:label"/>
			</xsl:attribute>
			<xsl:if test="@successful = 'false'">
				<failure/>
			</xsl:if>
		</testcase>
	</xsl:template>
	
	<xsl:function name="functx:index-of-string" as="xs:integer*" 
		>
		<xsl:param name="arg" as="xs:string?"/> 
		<xsl:param name="substring" as="xs:string"/> 
		
		<xsl:sequence select=" 
			if (contains($arg, $substring))
			then (string-length(substring-before($arg, $substring))+1,
			for $other in
			functx:index-of-string(substring-after($arg, $substring),
			$substring)
			return
			$other +
			string-length(substring-before($arg, $substring)) +
			string-length($substring))
			else ()
			"/>
		
	</xsl:function>
	
	<xsl:function name="functx:index-of-string-last" as="xs:integer?" 
		>
		<xsl:param name="arg" as="xs:string?"/> 
		<xsl:param name="substring" as="xs:string"/> 
		
		<xsl:sequence select=" 
			functx:index-of-string($arg, $substring)[last()]
			"/>
		
	</xsl:function>
	
                

</xsl:stylesheet>
                
          
