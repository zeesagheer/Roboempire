import pl.autoempire.core.UnitSize;
import pl.autoempire.core.connection.DisconnectException;
import pl.autoempire.core.connection.ErrorCode;
import pl.autoempire.core.connection.WaitMsgThreadLockType;
import pl.autoempire.core.connection.WaitMsgTimeoutException;
import pl.autoempire.core.dict.DefenceToolUnit;
import pl.autoempire.core.dict.DefenceToolWID;
import pl.autoempire.core.dict.Dungeon;
import pl.autoempire.core.dict.Kingdom;
import pl.autoempire.core.dict.SoldierUnit;
import pl.autoempire.core.dict.SoldierWID;
import pl.autoempire.core.dict.SoldierWeaponType;
import pl.autoempire.core.gamefiles.DungeonInfo;
import pl.autoempire.core.json.JSONException;
import pl.autoempire.core.messages.MsgAction;
import pl.autoempire.core.messages.JSON.GGSEmpireJSONMsg;
import pl.autoempire.core.messages.JSON.MsgOutAIN;
import pl.autoempire.core.messages.JSON.MsgOutHGH;
import pl.autoempire.core.objects.ObjectAIN;
import pl.autoempire.core.objects.ObjectHGH;
import pl.autoempire.core.objects.ObjectHGHItem;
import pl.autoempire.core.objects.ObjectO;
import pl.autoempire.gui.AppData;

public class UtilityMain {
	private static int lincenseNr = 81210;
	public static void printBarronInfo(DungeonInfo dungeon) {
		System.out.printf("Tower lvl: %d \tKingdom: %s", dungeon.getCountVictories(), 
				Kingdom.getByRawType(dungeon.getKingdomId()).getDescription());
		printBarronInfoFlank("Left",dungeon.getSoldiersLeft(), dungeon.getToolsLeft());
		printBarronInfoFlank("Middle",dungeon.getSoldiersMiddle(), dungeon.getToolsMiddle());
		printBarronInfoFlank("Right",dungeon.getSoldiersRight(), dungeon.getToolsRight());
		printBarronInfoFlank("Courtyard",dungeon.getSoldiersCourtyard(),  dungeon.getToolsCourtyard());
		System.out.println();
	}
	private static void printBarronInfoFlank(String flank, UnitSize[] unitSizeSoldier, UnitSize[] unitSizeTool) {
		if(unitSizeSoldier.length !=0)
		{	
			System.out.printf("\n%s:",flank);
			for (UnitSize unit : unitSizeSoldier) {
				SoldierWID soldierWID = SoldierUnit.getByRawType(unit.getWID()).getWid();
				System.out.printf("%s(%s)-%d\t",
						soldierWID.getDescription(),
						(soldierWID.getWeaponType()==SoldierWeaponType.ST_MELEE)?"M":"R",
						unit.getSize());
			}
			if(!flank.equals("Courtyard"))
			{
				if(unitSizeTool.length !=0)
					System.out.println();
				for (UnitSize unit : unitSizeTool) {
					//System.out.println(unit.WID);
					DefenceToolWID defenceToolWID = DefenceToolUnit.getByRawType(unit.getWID()).getWid();
					String impact = null;
					switch(defenceToolWID.getImpactType())
					{
					case TIT_GATE_DEFENCE:impact="G";break;
					case TIT_WALL_DEFENCE:impact="W";break;
					case TIT_MOAT_DEFENCE:impact="Moat";break;
					case TIT_MELEE_DEFENCE:impact="M";break;
					case TIT_DISTANCE_DEFENCE:impact="R";break;
					default:
						impact="unknown";
					}
					System.out.printf("(%s)-%d\t",impact,unit.getSize());
				}
			}
		}
	}
	public static void printBaronByKID(Kingdom kingdom) {
		try {
			xmlFile.load();
			for(DungeonInfo dungeon : xmlFile.dungeons) {
				if(dungeon.getKingdomId()== kingdom.getKID())
				{
					printBarronInfo(dungeon);
					System.out.println();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static Dungeon getDungeon(int attackCount, Kingdom kingdom) {
		try {
			xmlFile.load();
			for(DungeonInfo dungeonInfo : xmlFile.dungeons) {
				if(dungeonInfo.getCountVictories() == attackCount && dungeonInfo.getKingdomId()== kingdom.getKID())
				{
					return new Dungeon(dungeonInfo.getKingdomId(),
							dungeonInfo.getLordId(), 
							dungeonInfo.getCountVictories(), 
							dungeonInfo.getSkipCosts(),
							dungeonInfo.getSoldiersLeft(),
							dungeonInfo.getSoldiersMiddle(),
							dungeonInfo.getSoldiersRight(),
							dungeonInfo.getSoldiersCourtyard(),
							dungeonInfo.getToolsLeft(),
							dungeonInfo.getToolsMiddle(),
							dungeonInfo.getToolsRight(),
							dungeonInfo.getToolsCourtyard());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	public static void printKeyByAlliance(String allianceName) {
		try {
			final MsgOutHGH msgHGH = new MsgOutHGH();
            msgHGH.setListType(11);
            msgHGH.setSearchValue(allianceName);
			GGSEmpireJSONMsg msgIn = (GGSEmpireJSONMsg)AppData.session.sendAndWaitForAnswer(msgHGH, WaitMsgThreadLockType.WAIT_FOR_ONE, MsgAction.hgh.name());
			
            final ObjectHGH msgInHGH = new ObjectHGH(msgIn.getContent());
            final ObjectHGHItem rankInfo = msgInHGH.getAlianceByName(allianceName);
            
            final MsgOutAIN msgAIN = new MsgOutAIN();
            msgAIN.setAlianceId(rankInfo.getAlianceId());
            msgIn = (GGSEmpireJSONMsg)AppData.session.sendAndWaitForAnswer(msgAIN, WaitMsgThreadLockType.WAIT_FOR_ONE, msgAIN.getAction());
            if (ErrorCode.ALL_OK == ErrorCode.getFromRetCode(msgIn.getRetCode())) {
                String type = "non";
    			
    			for (ObjectO objectO : new ObjectAIN(msgIn.getContent()).getAlianceInfo().getAlianceMembers()) {
    				//System.out.println(objectO.getName());
    				testing.makeKey("PC",objectO.getName(),lincenseNr++,true,type);
    				testing.makeKey("AND",objectO.getName(),lincenseNr++,true,type);
    			}
    			System.out.println("Generated For: "+ allianceName);
            }
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (WaitMsgTimeoutException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (DisconnectException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
}
