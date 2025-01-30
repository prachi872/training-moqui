import org.moqui.context.ExecutionContext
import org.moqui.entity.EntityCondition
import org.moqui.entity.EntityFind
import org.moqui.entity.EntityList
import org.moqui.entity.EntityValue

// org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger("findParty")

ExecutionContext ec = context.ec

// NOTE: doing a find with a static view-entity because the Entity Facade will only select the fields specified and the
//     join in the associated member-entities
EntityFind ef = ec.entity.find("partyEnttes.FindCustomerView").distinct(true)
// don't do distinct, SQL quandary with distinct, limited select, and order by with upper needing to be selected; seems to get good results in general without: .distinct(true)

ef.selectField("partyId")

if (partyId) { ef.condition(ec.entity.conditionFactory.makeCondition("partyId", EntityCondition.LIKE, (leadingWildcard ? "%" : "") + partyId + "%").ignoreCase()) }

if(infoStrng){ef.conditon(ec.entity.conditionFactory.makeCondition("infoString",EntityCondition.LIKE,(leadingWildcard?"%" :"")+infoStrng+"%").ignoreCase())}
if(firstName){ef.condition(ec.entity.conditionFactory.makeCondition("firstName",EntityCondition.LIKE,(leadingWildcard?"%" :"")+firstName+"%").ignoreCase())}
if(lastName){ef.condition(ec.entity.conditionFactory.makeCondition("lastName",EntityCondition.LIKE,(leadingWildcard?"%" :"")+lastName+"%").ignoreCase())}
if(telecomNumber){ef.condton(ec.entity.conditionFactory.makeCondition("telecomNUmber",EntityCondition.LIKE,(leadingWildcard?"%" :"")+contactNumber+"%").ignoreCase())}
if (postalCode) { ef.condition(ec.entity.conditionFactory.makeCondition("postalCode", EntityCondition.LIKE, (leadingWildcard ? "%" : "") + postalCode + "%").ignoreCase()) }

if (combinedName) {
    // support splitting by just one space for first/last names
    String fnSplit = combinedName
    String lnSplit = combinedName
    if (combinedName.contains(" ")) {
        fnSplit = combinedName.substring(0, combinedName.indexOf(" "))
        lnSplit = combinedName.substring(combinedName.indexOf(" ") + 1)
    }
    cnCondList = [
                  ec.entity.conditionFactory.makeCondition("firstName", EntityCondition.LIKE, (leadingWildcard ? "%" : "") + fnSplit + "%").ignoreCase(),
                  ec.entity.conditionFactory.makeCondition("lastName", EntityCondition.LIKE, (leadingWildcard ? "%" : "") + lnSplit + "%").ignoreCase()]
    ef.condition(ec.entity.conditionFactory.makeCondition(cnCondList, EntityCondition.OR))
}

if (!pageNoLimit) { ef.offset(pageIndex as int, pageSize as int); ef.limit(pageSize as int) }

// logger.warn("======= find#Party cond: ${ef.getWhereEntityCondition()}")

partyIdList = []
EntityList el = ef.list()
for (EntityValue ev in el) partyIdList.add(ev.partyId)

partyIdListCount = ef.count()
partyIdListPageIndex = ef.pageIndex
partyIdListPageSize = ef.pageSize
partyIdListPageMaxIndex = ((BigDecimal) (partyIdListCount - 1)).divide(partyIdListPageSize, 0, BigDecimal.ROUND_DOWN) as int
partyIdListPageRangeLow = partyIdListPageIndex * partyIdListPageSize + 1
partyIdListPageRangeHigh = (partyIdListPageIndex * partyIdListPageSize) + partyIdListPageSize
if (partyIdListPageRangeHigh > partyIdListCount) partyIdListPageRangeHigh = partyIdListCount