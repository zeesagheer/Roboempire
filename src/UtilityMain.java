import pl.autoempire.core.UnitSize;
import pl.autoempire.core.dict.DefenceToolUnit;
import pl.autoempire.core.dict.DefenceToolWID;
import pl.autoempire.core.dict.Kingdom;
import pl.autoempire.core.dict.SoldierUnit;
import pl.autoempire.core.dict.SoldierWID;
import pl.autoempire.core.dict.SoldierWeaponType;
import pl.autoempire.core.gamefiles.DungeonInfo;

public class UtilityMain {
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
}
