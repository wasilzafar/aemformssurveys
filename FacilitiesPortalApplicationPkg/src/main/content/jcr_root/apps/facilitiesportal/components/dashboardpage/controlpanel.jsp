<%@taglib prefix="cq" uri="http://www.day.com/taglibs/cq/1.0" %>
<%@include file="/libs/foundation/global.jsp"%>
<div id="controls">
<br>
<form id="controlpanelform" class="dark-matter" method="post">
<fieldset>
    <legend>  Dashboard for Surveys  </legend><br/>
    <button class="button" onclick="displaySurveys()" type="button">Browse Surveys ...</button>
  <br/><br/>
<button class="button" id="submitButton">Show</button>
 </fieldset>
</form>
<div id="surveyContainer" style="display:none;position: absolute;width: 50%;left: 25%;top: 25%;margin: 0 auto;background-color: white;z-index: 100">
<table id="surveys">
  <tr>
    <th>ID</th>
    <th>Description</th>
    <th>Form</th>
    <th></th>
  </tr>
</table>
<input id="selectbutton" class="button" type="button" onclick="validateSelectedSurveys()" value="Done">&nbsp;&nbsp;&nbsp;&nbsp;<input id="cancelbutton" class="button" type="button" value="Cancel">
</div>
</div>