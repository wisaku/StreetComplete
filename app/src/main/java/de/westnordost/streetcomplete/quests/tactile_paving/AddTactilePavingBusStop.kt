package de.westnordost.streetcomplete.quests.tactile_paving

import de.westnordost.streetcomplete.R
import de.westnordost.streetcomplete.data.meta.updateWithCheckDate
import de.westnordost.streetcomplete.data.osm.osmquest.SimpleOverpassQuestType
import de.westnordost.streetcomplete.data.osm.changes.StringMapChangesBuilder
import de.westnordost.streetcomplete.data.osm.mapdata.OverpassMapDataAndGeometryApi
import de.westnordost.streetcomplete.data.quest.NoCountriesExcept
import de.westnordost.streetcomplete.ktx.toYesNo
import de.westnordost.streetcomplete.settings.ResurveyIntervalsStore

class AddTactilePavingBusStop(o: OverpassMapDataAndGeometryApi, r: ResurveyIntervalsStore)
    : SimpleOverpassQuestType<Boolean>(o) {

    override val tagFilters = """
        nodes, ways with
        (
          (public_transport = platform and (bus = yes or trolleybus = yes or tram = yes)) 
          or 
          (highway = bus_stop and public_transport != stop_position)
        )
        and physically_present != no and naptan:BusStopType != HAR
        and (
          !tactile_paving
          or tactile_paving = no and tactile_paving older today -${r * 4} years
          or tactile_paving older today -${r * 8} years
        )
    """
    override val commitMessage = "Add tactile pavings on bus stops"
    override val wikiLink = "Key:tactile_paving"
    override val icon = R.drawable.ic_quest_blind_bus

    // See overview here: https://ent8r.github.io/blacklistr/?streetcomplete=tactile_paving/AddTactilePavingBusStop.kt
    // #750
    override val enabledInCountries = NoCountriesExcept(
            // Europe
            "NO", "SE",
            "GB", "IE", "NL", "BE", "FR", "ES",
            "DE", "PL", "CZ", "SK", "HU", "AT", "CH",
            "LV", "LT", "LU", "EE", "RU",
            // America
            "US", "CA", "AR",
            // Asia
            "HK", "SG", "KR", "JP",
            // Oceania
            "AU", "NZ"
    )

    override fun getTitle(tags: Map<String, String>): Int {
        val hasName = tags.containsKey("name")
        val isTram = tags["tram"] == "yes"
        return if (isTram) {
            if (hasName) R.string.quest_tactilePaving_title_name_tram
            else         R.string.quest_tactilePaving_title_tram
        } else {
            if (hasName) R.string.quest_tactilePaving_title_name_bus
            else         R.string.quest_tactilePaving_title_bus
        }
    }

    override fun createForm() = TactilePavingForm()

    override fun applyAnswerTo(answer: Boolean, changes: StringMapChangesBuilder) {
        changes.updateWithCheckDate("tactile_paving", answer.toYesNo())
    }
}
