<%@taglib prefix="cq" uri="http://www.day.com/taglibs/cq/1.0" %>
<%@include file="/libs/foundation/global.jsp"%>
<cq:defineObjects/>
<cq:includeClientLib categories="cq.jquery"/>
<div id="panelchart">
<cq:include script="controlpanel.jsp"/>
</div>
<br>
<div id="containerChart" style="display:none">
</div>
<cq:includeClientLib categories="apps.facilitiesportal"/>