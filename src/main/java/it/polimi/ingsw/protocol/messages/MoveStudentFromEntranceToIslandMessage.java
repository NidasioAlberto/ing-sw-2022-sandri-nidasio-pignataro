package it.polimi.ingsw.protocol.messages;

import it.polimi.ingsw.controller.GameActionHandler;
import it.polimi.ingsw.model.BaseGameAction;
import it.polimi.ingsw.model.SchoolColor;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Message related to the movement of a student from entrance to an island.
 */
public class MoveStudentFromEntranceToIslandMessage extends ActionMessage
{
    List<SchoolColor> selectedColors;
    int selectedIsland;

    protected MoveStudentFromEntranceToIslandMessage(JSONObject actionJson) throws JSONException
    {
        selectedColors = new ArrayList<>();

        JSONArray colors = actionJson.getJSONArray("selectedColors");
        for (int i = 0; i < colors.length(); i++)
            selectedColors.add(SchoolColor.valueOf(colors.getString(i)));

        selectedIsland = actionJson.getInt("selectedIsland");
    }

    public void applyAction(GameActionHandler handler)
    {
        checkHandler(handler);
        handler.moveStudentFromEntranceToIsland(selectedColors, selectedIsland);
    }

    public BaseGameAction getBaseGameAction()
    {
        return BaseGameAction.MOVE_STUDENT_FROM_ENTRANCE_TO_ISLAND;
    }
}
