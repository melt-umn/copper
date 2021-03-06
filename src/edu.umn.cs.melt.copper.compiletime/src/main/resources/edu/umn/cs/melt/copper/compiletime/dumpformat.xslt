<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:copper="http://melt.cs.umn.edu/copper/xmlns/xmldump/0.9" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<xsl:output method="html" indent="yes"/>

<xsl:template match="/copper:CopperXMLDump">
<html xmlns="http://www.w3.org/1999/xhtml"
    version="-//W3C//DTD XHTML 2.0//EN" xml:lang="en">
<head>
<title>Copper HTML dump</title>
<style type="text/css">
/* div.page      { width: 800px; } */
table.symtable,
table.dfatable  { border: 1px solid #000000; border-collapse: collapse; }
span.lookaheadset { background-color: #d0d0d0; padding-left: 4pt; padding-right: 4pt; }
table.symtable tr.trodd  { background-color: #d0d0d0; }
table.symtable tr.trconflicted { background-color: #ff0000; }
table.symtable td,th { border: 1px solid #000000; padding: 0.3em 0.3em; }
td.fixedcell { text-align: left; font-family: monospace; }
td.fixedfccell { text-align: center; font-family: monospace; }
td.varcell   { text-align: center; }
a[href] { color: #0000c0; text-decoration: none; }
a[href]:hover { background: #000000; color: #7f7fff; /* font-weight: bold; */ }
a[href]:hover.toclink { background: #d0d0d0; color: #2020c0; /* font-weight: normal; */ }
a[href].genlink { /* padding: 0.5em 0.5em; */ }
a[href].tablelink { /* padding: 0.5em 1em; */ padding-left: 4pt; padding-right: 4pt; }
a[href].tablelink_conflicted { /* padding: 0.5em 1em; */ background-color: #ff0000;  padding-left: 4pt; padding-right: 4pt; }
a[href].tablelink_conflicted:hover { background-color: #000000; }
table.dfatable td.dfacell   { border: 0px; padding: 0.5em; font-family: monospace; }
span.grayed { color: #808080; font-style: italic; }
</style>
</head>
<body>
<div class="page">

<a name="toc"/>
<h1>Table of contents</h1>
<ul>
<li><a href="#terminals" class="toclink">Terminals</a></li>
<li><a href="#nonterminals" class="toclink">Nonterminals</a></li>
<li><a href="#productions" class="toclink">Productions</a></li>
<li><a href="#disambig_groups" class="toclink">Disambiguation functions/groups</a></li>
<xsl:if test="./copper:PrecedenceGraph/copper:img"><li><a href="#precgraph" class="toclink">Precedence relation graph</a></li></xsl:if>
<li><a href="#lalr_dfa" class="toclink">LALR(1) DFA</a></li>
<li><a href="#parsetable" class="toclink">Parse and goto tables</a></li>
</ul>

<a name="terminals"/>
<h1>Terminals</h1>
<table class="symtable">
<tr>
<th>ID</th>
<th>Name</th>
<th>Submits to<br/>(&lt;)</th>
<th>Dominates<br/>(&gt;)</th>
<th>Internal name</th>
<th>In grammar</th>
</tr>
   <xsl:apply-templates select="copper:Terminal">
       <xsl:sort select="string-length(@tag)"/>
       <xsl:sort select="@tag"/>
   </xsl:apply-templates>
</table>
<a name="nonterminals"/>
<h1>Nonterminals</h1>
<table class="symtable">
<tr>
<th>ID</th>
<th>Name</th>
<th>Internal name</th>
<th>In grammar</th>
</tr>
   <xsl:apply-templates select="copper:Nonterminal">
       <xsl:sort select="string-length(@tag)"/>
       <xsl:sort select="@tag"/>
   </xsl:apply-templates>
</table>
<a name="productions"/>
<h1>Productions</h1>
<table class="symtable">
<tr>
<th>ID</th>
<th>Signature</th>
<th>In grammar</th>
</tr>
   <xsl:apply-templates select="copper:Production">
       <xsl:sort select="string-length(@tag)"/>
       <xsl:sort select="@tag"/>
   </xsl:apply-templates>
</table>
<a name="disambig_groups"/>
<h1>Disambiguation functions/groups</h1>
<xsl:choose>
<xsl:when test="/copper:CopperXMLDump/copper:DisambiguationFunction">
   <table class="symtable">
   <tr>
   <th>ID</th>
   <th>Name</th>
   <th>Members</th>
   </tr>
      <xsl:apply-templates select="copper:DisambiguationFunction">
          <xsl:sort select="string-length(@tag)"/>
          <xsl:sort select="@tag"/>
      </xsl:apply-templates>
   </table>
</xsl:when>
<xsl:otherwise>
<p>No disambiguation functions.</p>
</xsl:otherwise>
</xsl:choose>
<a name="precgraph"/>
<xsl:if test="./copper:PrecedenceGraph/img"><h1>Precedence relation graph</h1></xsl:if>
<!-- Put a generated SVG graph in here? -->
<xsl:apply-templates select="copper:PrecedenceGraph"/>
<a name="lalr_dfa"/>
<h1>LALR(1) DFA</h1>
   <xsl:apply-templates select="copper:LALR_DFA"/>

<a name="parsetable"/>
<h1>Parse and goto tables</h1>
   <xsl:apply-templates select="copper:LRParseTable"/>

</div>
</body>
</html>

</xsl:template>

<xsl:template match="copper:Terminal">
<tr>
<xsl:choose>
  <xsl:when test="position() mod 2 = 0">
     <xsl:attribute name="class">treven</xsl:attribute>
  </xsl:when>
  <xsl:otherwise>
     <xsl:attribute name="class">trodd</xsl:attribute>
  </xsl:otherwise>
</xsl:choose>
<td class="varcell">
<a name="{@tag}"/>
<xsl:value-of select="@tag"/></td>
<td class="fixedcell">
	<xsl:call-template name="getDisplayName">
		<xsl:with-param name="element" select="."/>
	</xsl:call-template>
</td>
<td class="fixedcell">
    <xsl:variable name="this" select="@tag"/>
    <xsl:variable name="cardinality" select="count(/copper:CopperXMLDump/copper:PrecedenceGraph/copper:Edge[@submits=$this])"/>
    <xsl:for-each select="/copper:CopperXMLDump/copper:PrecedenceGraph/copper:Edge[@submits=$this]">
        <xsl:sort select="string-length(@dominates)"/>
        <xsl:sort select="@dominates"/>
        <xsl:variable name="dominates" select="@dominates"/>
        <a href="#{$dominates}" class="genlink"><xsl:call-template name="getDisplayName"><xsl:with-param name="element" select="/copper:CopperXMLDump/copper:Terminal[@tag=$dominates]"/></xsl:call-template></a>
        <xsl:if test="position() != $cardinality">, </xsl:if>
    </xsl:for-each>
</td>
<td class="fixedcell">
    <xsl:variable name="this" select="@tag"/>
    <xsl:variable name="cardinality" select="count(/copper:CopperXMLDump/copper:PrecedenceGraph/copper:Edge[@dominates=$this])"/>
    <xsl:for-each select="/copper:CopperXMLDump/copper:PrecedenceGraph/copper:Edge[@dominates=$this]">
        <xsl:sort select="string-length(@submits)"/>
        <xsl:sort select="@submits"/>
        <xsl:variable name="submits" select="@submits"/>
        <a href="#{$submits}" class="genlink"><xsl:call-template name="getDisplayName"><xsl:with-param name="element" select="/copper:CopperXMLDump/copper:Terminal[@tag=$submits]"/></xsl:call-template></a>
        <xsl:if test="position() != $cardinality">, </xsl:if>
    </xsl:for-each>
</td>
<td class="fixedcell">
	<xsl:choose>
		<xsl:when test="copper:DisplayName">
			<xsl:value-of select="@id"/>
		</xsl:when>
		<xsl:otherwise>
			<span class="grayed">Same</span>
		</xsl:otherwise>
	</xsl:choose>
</td>
<td class="fixedcell">
<xsl:variable name="owner" select="@owner"/>
<xsl:call-template name="getDisplayName"><xsl:with-param name="element" select="/copper:CopperXMLDump/copper:Grammar[@tag=$owner]"/></xsl:call-template>
</td>
</tr>
</xsl:template>

<xsl:template match="copper:Nonterminal">
<tr>
<xsl:choose>
  <xsl:when test="position() mod 2 = 0">
     <xsl:attribute name="class">treven</xsl:attribute>
  </xsl:when>
  <xsl:otherwise>
     <xsl:attribute name="class">trodd</xsl:attribute>
  </xsl:otherwise>
</xsl:choose>
<td class="varcell">
<a name="{@tag}"/>
<xsl:value-of select="@tag"/></td>
<td class="fixedcell">
	<xsl:call-template name="getDisplayName">
		<xsl:with-param name="element" select="."/>
	</xsl:call-template>
</td>
<td class="fixedcell">
	<xsl:choose>
		<xsl:when test="copper:DisplayName">
			<xsl:value-of select="@id"/>
		</xsl:when>
		<xsl:otherwise>
			<span class="grayed">Same</span>
		</xsl:otherwise>
	</xsl:choose>
</td>
<td class="fixedcell">
<xsl:variable name="owner" select="@owner"/>
<xsl:call-template name="getDisplayName"><xsl:with-param name="element" select="/copper:CopperXMLDump/copper:Grammar[@tag=$owner]"/></xsl:call-template>
</td>
</tr>
</xsl:template>

<xsl:template match="copper:Production">
<tr>
<xsl:choose>
  <xsl:when test="position() mod 2 = 0">
     <xsl:attribute name="class">treven</xsl:attribute>
  </xsl:when>
  <xsl:otherwise>
     <xsl:attribute name="class">trodd</xsl:attribute>
  </xsl:otherwise>
</xsl:choose>
<td class="varcell">
<a name="{@tag}"/>
<xsl:value-of select="@tag"/></td>
<td class="fixedcell">
<xsl:variable name="lhs" select="./copper:LHS"/>
<xsl:variable name="lhsid" select="/copper:CopperXMLDump/copper:Nonterminal[@tag=$lhs]"/>
<a href="#{$lhsid/@tag}" class="genlink"><xsl:call-template name="getDisplayName"><xsl:with-param name="element" select="$lhsid"/></xsl:call-template></a>
::=
<xsl:for-each select="./copper:RHS/copper:Terminal|./copper:RHS/copper:Nonterminal">
    <xsl:variable name="this" select="./@ref"/>

    <xsl:variable name="thisid" select="/copper:CopperXMLDump/copper:Terminal[@tag=$this]|/copper:CopperXMLDump/copper:Nonterminal[@tag=$this]"/>
    <a href="#{$thisid/@tag}" class="genlink"><xsl:call-template name="getDisplayName"><xsl:with-param name="element" select="$thisid"/></xsl:call-template></a><xsl:text> </xsl:text>
    <!--<xsl:text> </xsl:text>-->
</xsl:for-each>
</td>
<td class="fixedcell">
<xsl:variable name="owner" select="@owner"/>
<xsl:call-template name="getDisplayName"><xsl:with-param name="element" select="/copper:CopperXMLDump/copper:Grammar[@tag=$owner]"/></xsl:call-template>
</td>
</tr>
</xsl:template>

<xsl:template match="copper:DisambiguationFunction">
<tr>
<xsl:choose>
  <xsl:when test="position() mod 2 = 0">
     <xsl:attribute name="class">treven</xsl:attribute>
  </xsl:when>
  <xsl:otherwise>
     <xsl:attribute name="class">trodd</xsl:attribute>
  </xsl:otherwise>
</xsl:choose>
<td class="varcell">
<a name="{@tag}"/>
<xsl:value-of select="@tag"/></td>
<td class="fixedcell"><xsl:value-of select="@id"/></td>
<td class="fixedcell">
<xsl:variable name="cardinality" select="count(./copper:Member)"/>
<xsl:for-each select="./copper:Member">
    <xsl:sort select="string-length(./@ref)"/>
    <xsl:sort select="./@ref"/>
    <xsl:variable name="this" select="./@ref"/>
    <a href="#{$this}" class="genlink">
    	<xsl:call-template name="getDisplayName"><xsl:with-param name="element" select="/copper:CopperXMLDump/copper:Terminal[@tag=$this]"/></xsl:call-template>
    </a>
    <xsl:if test="position() != $cardinality">, </xsl:if>
</xsl:for-each>
</td>
</tr>
</xsl:template>

<xsl:template match="copper:LALR_DFA">
    <h2>States index</h2>
    <xsl:for-each select="./copper:State">
        <xsl:sort select="string-length(@tag)"/>
        <xsl:sort select="@tag"/>
        <a href="#{@tag}" class="tablelink"><xsl:value-of select="@id"/></a><xsl:text> </xsl:text>
    </xsl:for-each>
    <!-- <h2>States</h2> -->
    <xsl:for-each select="./copper:State">
        <xsl:sort select="string-length(@tag)"/>
        <xsl:sort select="@tag"/>
        <a name="{@tag}"/><h2>State <xsl:value-of select="@id"/></h2>
        <table class="dfatable">
        <xsl:for-each select="./copper:Item">
             <xsl:sort select="string-length(@production)"/>
             <xsl:sort select="@production"/>
             <xsl:variable name="marker" select="@marker"/>
             <tr>
             <td class="dfacell">
             <xsl:variable name="prodtag" select="@production"/>
             <xsl:variable name="prod" select="/copper:CopperXMLDump/copper:Production[@tag=$prodtag]"/>
             <xsl:variable name="lhs" select="$prod/copper:LHS"/>
             <xsl:variable name="lhsid" select="/copper:CopperXMLDump/copper:Nonterminal[@tag=$lhs]"/>
             <a href="#{$lhsid/@tag}" class="genlink"><xsl:call-template name="getDisplayName"><xsl:with-param name="element" select="$lhsid"/></xsl:call-template></a>
             ::=
             <xsl:if test="$marker = 0">&#x25CF; <!--(*)--></xsl:if>
             <xsl:for-each select="$prod/copper:RHS/copper:Terminal|$prod/copper:RHS/copper:Nonterminal">
                <xsl:variable name="this" select="./@ref"/>

                <xsl:variable name="thisid" select="/copper:CopperXMLDump/copper:Terminal[@tag=$this]|/copper:CopperXMLDump/copper:Nonterminal[@tag=$this]"/>
                <a href="#{$thisid/@tag}" class="genlink"><xsl:call-template name="getDisplayName"><xsl:with-param name="element" select="$thisid"/></xsl:call-template></a><xsl:text> </xsl:text>
               <!--<xsl:text> </xsl:text>-->
               <xsl:if test="position() = $marker">&#x25CF; <!--(*)--></xsl:if>
             </xsl:for-each>
             , [ 
             <xsl:variable name="cardinality" select="count(./copper:Lookahead)"/>
             <xsl:if test="$cardinality &gt; 0">
             <span class="lookaheadset">
             <xsl:for-each select="./copper:Lookahead">
                <xsl:variable name="this" select="./@ref"/>
                <a href="#{$this}" class="genlink">
                	<xsl:call-template name="getDisplayName">
                		<xsl:with-param name="element" select="/copper:CopperXMLDump/copper:Terminal[@tag=$this]"/>
                	</xsl:call-template>
                </a>
                <xsl:if test="position() != $cardinality">, </xsl:if>
             </xsl:for-each>
             </span></xsl:if> ]
             </td>
             </tr>
        </xsl:for-each>
        </table>
        <xsl:if test="./copper:Transition">
           <table class="symtable">
           <tr>
           <th>On symbol</th>
           <th>Transition to</th>
           </tr>
           <xsl:for-each select="./copper:Transition">
               <xsl:sort select="string-length(@label)"/>
               <xsl:sort select="@label"/>
               <xsl:variable name="label" select="@label"/>
               <xsl:variable name="dest" select="@dest"/>
               <tr>
               <xsl:choose>
                  <xsl:when test="position() mod 2 = 0">
                     <xsl:attribute name="class">treven</xsl:attribute>
                  </xsl:when>
                  <xsl:otherwise>
                     <xsl:attribute name="class">trodd</xsl:attribute>
                  </xsl:otherwise>
               </xsl:choose>
               <td class="fixedfccell">
                   <xsl:variable name="labelid" select="/copper:CopperXMLDump/copper:Terminal[@tag=$label]|/copper:CopperXMLDump/copper:Nonterminal[@tag=$label]"/>
                   <a href="#{$labelid/@tag}" class="genlink"><xsl:call-template name="getDisplayName"><xsl:with-param name="element" select="$labelid"/></xsl:call-template></a>
               </td>
               <td class="fixedfccell">
                  <a href="#{$dest}" class="genlink">State
                  <xsl:call-template name="getDisplayName"><xsl:with-param name="element" select="/copper:CopperXMLDump/copper:LALR_DFA/copper:State[@tag=$dest]"/></xsl:call-template></a>
               </td>
               </tr>
           </xsl:for-each>
           </table>
        </xsl:if>
    </xsl:for-each>
</xsl:template>

<xsl:template match="copper:LRParseTable">
    <h2>States index</h2>
    <xsl:for-each select="./copper:State">
        <xsl:sort select="string-length(@tag)"/>
        <xsl:sort select="@tag"/>
        <a href="#{@tag}">
        	<xsl:choose>
        		<xsl:when test="copper:ParseCell[count(copper:Shift|copper:Reduce|copper:Accept) &gt; 1]">
        			<xsl:attribute name="class">tablelink_conflicted</xsl:attribute>
        		</xsl:when>
        		<xsl:otherwise>
        			<xsl:attribute name="class">tablelink</xsl:attribute>
        		</xsl:otherwise>
        	</xsl:choose>
        	<xsl:value-of select="@id"/>
        </a><xsl:text> </xsl:text>
    </xsl:for-each>
    <!-- <h2>States</h2> -->
    <xsl:for-each select="./copper:State">
        <xsl:sort select="string-length(@tag)"/>
        <xsl:sort select="@tag"/>
        <a name="{@tag}"/><h2>State <xsl:value-of select="@id"/></h2>
        <xsl:if test="./copper:ParseCell">
	        <table class="symtable">
	        <caption><h4>Parse actions</h4></caption>
	        <tr>
	        <th>Cell</th>
	        <th>Action</th>
	        </tr>
	        <xsl:for-each select="./copper:ParseCell">
                    <xsl:sort select="string-length(@id)"/>
	            <xsl:sort select="@id"/>
	            <tr> 
	            <xsl:choose>
	               <xsl:when test="count(copper:Shift|copper:Reduce|copper:Accept) &gt; 1">
	                  <xsl:attribute name="class">trconflicted</xsl:attribute>
	               </xsl:when>
	               <xsl:when test="position() mod 2 = 0">
	                  <xsl:attribute name="class">treven</xsl:attribute>
	               </xsl:when>
	               <xsl:otherwise>
	                  <xsl:attribute name="class">trodd</xsl:attribute>
	               </xsl:otherwise>
	            </xsl:choose>
	            <td class="fixedfccell">
		           <xsl:variable name="this" select="@id"/>
		           <xsl:variable name="layoutid" select="/copper:CopperXMLDump/copper:Terminal[@tag=$this]"/>
		           <a href="#{$layoutid/@tag}" class="genlink"><xsl:call-template name="getDisplayName"><xsl:with-param name="element" select="$layoutid"/></xsl:call-template><!--<xsl:value-of select="$layoutid/@id"/>--></a>
	            </td>
			    <td class="fixedcell">
                	<xsl:apply-templates/>
               	</td>
	            </tr>
	        </xsl:for-each>
	        </table>
        </xsl:if>
        <xsl:if test="./copper:GotoCell">
	        <table class="symtable">
	        <caption><h4>Goto actions</h4></caption>
	        <tr>
	        <th>Cell</th>
	        <th>Action</th>
	        </tr>
	        <xsl:for-each select="./copper:GotoCell">
                    <xsl:sort select="string-length(@id)"/>
	            <xsl:sort select="@id"/>
	            <tr> 
	            <xsl:choose>
	               <xsl:when test="position() mod 2 = 0">
	                  <xsl:attribute name="class">treven</xsl:attribute>
	               </xsl:when>
	               <xsl:otherwise>
	                  <xsl:attribute name="class">trodd</xsl:attribute>
	               </xsl:otherwise>
	            </xsl:choose>
	            <td class="fixedfccell">
		           <xsl:variable name="this" select="@id"/>
		           <xsl:variable name="layoutid" select="/copper:CopperXMLDump/copper:Nonterminal[@tag=$this]"/>
		           <a href="#{$layoutid/@tag}" class="genlink"><xsl:call-template name="getDisplayName"><xsl:with-param name="element" select="$layoutid"/></xsl:call-template></a>
	            </td>
	            <td class="fixedcell">
	                   <xsl:apply-templates/>
	            </td>
	            </tr>
	        </xsl:for-each>
	        </table>
        </xsl:if>
        <xsl:if test="./copper:Layout">
	    <table class="symtable">
	        <caption><h4>Layout</h4></caption>
	        <tr>
	        <th>Layout</th>
	        <th>Can come before</th>
	        </tr>
	        <xsl:for-each select="./copper:Layout">
                   <xsl:sort select="string-length(@tag)"/>
	           <xsl:sort select="@tag"/>
	           <tr>
                   <xsl:choose>
                      <xsl:when test="position() mod 2 = 0">
                         <xsl:attribute name="class">treven</xsl:attribute>
                      </xsl:when>
                      <xsl:otherwise>
                         <xsl:attribute name="class">trodd</xsl:attribute>
                      </xsl:otherwise>
                   </xsl:choose>
	           <td class="fixedfccell">
	           <xsl:variable name="this" select="@tag"/>
	           <xsl:variable name="layoutid" select="/copper:CopperXMLDump/copper:Terminal[@tag=$this]"/>
	           <a href="#{$layoutid/@tag}" class="genlink"><xsl:call-template name="getDisplayName"><xsl:with-param name="element" select="$layoutid"/></xsl:call-template></a>
	           </td>
	           <td class="fixedcell">
	           <xsl:variable name="cardinality" select="count(./copper:Follow)"/>
	           <xsl:for-each select="./copper:Follow">
	               <xsl:variable name="this" select="./@ref"/>
	               <xsl:variable name="layoutid" select="/copper:CopperXMLDump/copper:Terminal[@tag=$this]"/>
	               <a href="#{$layoutid/@tag}" class="genlink"><xsl:call-template name="getDisplayName"><xsl:with-param name="element" select="$layoutid"/></xsl:call-template></a>
	               <xsl:if test="position() != $cardinality">, </xsl:if>
	           </xsl:for-each>
	           </td>
	           </tr>
	        </xsl:for-each>
	        </table>
        </xsl:if>
        <xsl:if test="./copper:Prefix">
           <table class="symtable">
           <caption><h4>Transparent prefixes</h4></caption>
           <tr>
           <th>Prefix</th>
           <th>Can come before</th>
           </tr>
           <xsl:for-each select="./copper:Prefix">
              <xsl:sort select="string-length(@tag)"/>
              <xsl:sort select="@tag"/>
              <tr>
                <xsl:choose>
                   <xsl:when test="position() mod 2 = 0">
                      <xsl:attribute name="class">treven</xsl:attribute>
                   </xsl:when>
                   <xsl:otherwise>
                      <xsl:attribute name="class">trodd</xsl:attribute>
                   </xsl:otherwise>
                </xsl:choose>
              <td class="fixedfccell">
              <xsl:variable name="this" select="@tag"/>
              <xsl:variable name="prefixid" select="/copper:CopperXMLDump/copper:Terminal[@tag=$this]"/>
              <a href="#{$prefixid/@tag}" class="genlink"><xsl:call-template name="getDisplayName"><xsl:with-param name="element" select="$prefixid"/></xsl:call-template></a>
              </td>
              <td class="fixedcell">
                 <xsl:variable name="cardinality" select="count(./copper:Follow)"/>
                 <xsl:for-each select="./copper:Follow">
                  <xsl:variable name="this" select="./@ref"/>
                  <xsl:variable name="prefixid" select="/copper:CopperXMLDump/copper:Terminal[@tag=$this]"/>
                  <a href="#{$prefixid/@tag}" class="genlink"><xsl:call-template name="getDisplayName"><xsl:with-param name="element" select="$prefixid"/></xsl:call-template></a>
                  <xsl:if test="position() != $cardinality">, </xsl:if>
              </xsl:for-each>
              </td>
              </tr>
           </xsl:for-each>
           </table>
        </xsl:if>
    </xsl:for-each>
</xsl:template>

<xsl:template match="copper:Accept">
    Accept<br/>
</xsl:template>

<xsl:template match="copper:Shift">
    Shift to <xsl:variable name="dest" select="@dest"/>
    <a href="#{$dest}" class="genlink">state
    <xsl:value-of select="/copper:CopperXMLDump/copper:LRParseTable/copper:State[@tag=$dest]/@id"/></a><br/>
</xsl:template>

<xsl:template match="copper:Goto">
    Goto <xsl:variable name="dest" select="@dest"/>
    <a href="#{$dest}" class="genlink">state
    <xsl:value-of select="/copper:CopperXMLDump/copper:LRParseTable/copper:State[@tag=$dest]/@id"/></a><br/>
</xsl:template>

<xsl:template match="copper:Reduce">
    Reduce with <xsl:variable name="prod" select="@prod"/>
    <a href="#{$prod}" class="genlink">production
    <xsl:value-of select="/copper:CopperXMLDump/copper:Production[@tag=$prod]/@tag"/></a><br/>
</xsl:template>

<xsl:template name="getDisplayName">
	<xsl:param name="element"/>
	<xsl:choose>
		<xsl:when test="$element/copper:DisplayName">
			<xsl:value-of select="$element/copper:DisplayName"/>
		</xsl:when>
		<xsl:otherwise>
			<xsl:value-of select="$element/@id"/>
		</xsl:otherwise>
	</xsl:choose>
</xsl:template>

<xsl:template match="copper:PrecedenceGraph">
	<xsl:apply-templates select="img"/>
</xsl:template>

<xsl:template match="@*|node()">
    <xsl:copy>
        <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
</xsl:template>

</xsl:stylesheet>
