package parsers;

// A simplified grammar of blazonry (description language of heraldic devices);
// an extreme example of context-aware scanning.


%%
%parser BlazonGrammarParser

%lex{
	class enum, kwd, general;

    ignore terminal ws ::= /[ \t\n]+|([ \t\n]*\([^\n)]*\)[ \t\n]*)/;

    terminal color ::= /([Aa]zure)|([Gg]ules)|([Pp]urpure)|([Ss]able)|([Vv]ert)/ in (kwd);
    terminal metal ::= /([Aa]rgent)|([Oo]r)/ in (kwd);
    terminal stain ::= /([Oo]range)|([Mm]urrey)|([Ss]anguine)|([Tt]enne)/ in (kwd);
    terminal fur ::= /([Ee]rmine)|([Ee]rmines)|([Ee]rminois)|([Pp]ean)|([Vv]air)|([Cc]ountervair)|([Vv]air-en-point)|([Vv]air-in-pale)|([Vv]airy)|([Cc]ountervairy)|([Vv]airy-en-point)|([Vv]airy-in-pale)|([Pp]otent)|([Cc]ounterpotent)|([Pp]otent-en-point)|([Pp]apellone)|([Pp]lumete)/ in (kwd);
    terminal proper ::= /[Pp]roper/ in (kwd);
    terminal counterchanged ::= /[Cc]ounterchanged/ in (kwd);

    terminal line ::= /([Aa]ngled)|([Aa]rched)|([Dd]ouble-arched)|([Bb]evilled)|([Bb]retessed)|([Cc]ounterembattled)|([Cc]lover-leaf)|([Cc]rested)|([Dd]entilly)|([Dd]ancetty)|([Dd]ovetailed)|([Ee]cartelle)|([Ee]mbattled)|([Ee]mbattled-grady)|([Ee]ngrailed)|([Ff]ir-tree)|([Ff]ir-twig)|([Ff]lory)|([Ii]ndented)|([Ii]nvected)|([Nn]ebuly)|([Nn]owy)|([Pp]otenty)|([Rr]aguly)|([Rr]ayonny)|([Uu]ndy)|([Uu]rdy)|([Ww]avy)/ in (kwd);

    terminal fullsize_ordinary ::= /([Ff]ess)|([Pp]ale)|([Tt]ierce)|([Bb]end)|([Bb]end-sinister)|([Cc]hevron)|([Cc]hevron-inverted)|([Ss]altire)|([Cc]ross)/ in (enum);
    terminal miniature_ordinary ::= /([Bb]ar)|([Cc]loset)|([Bb]arrulet)|([Pp]allet)|([Bb]endlet)|([Rr]ibbon)|([Ss]carpe)|([Rr]ibbon-sinister)|([Cc]hevronel)|([[Cc]hevronel-inverted)|([Ff]illet-cross)|([Ff]illet-saltire)/ in (enum);
    // terminal single_ordinary ::= /[:fullsize_ordinary:]|[:miniature_ordinary:]/ in (kwd);
    terminal single_ordinary ::= /([Ff]ess)|([Pp]ale)|([Tt]ierce)|([Bb]end)|([Bb]end-sinister)|([Cc]hevron)|([Cc]hevron-inverted)|([Ss]altire)|([Cc]ross)|([Bb]ar)|([Cc]loset)|([Bb]arrulet)|([Pp]allet)|([Bb]endlet)|([Rr]ibbon)|([Ss]carpe)|([Rr]ibbon-sinister)|([Cc]hevronel)|([[Cc]hevronel-inverted)|([Ff]illet-cross)|([Ff]illet-saltire)/ in (kwd);
    terminal compound_ordinary ::= /([Bb]ars)|([Cc]losets)|([Bb]arrulets)|([Pp]allets)|([Bb]endlets)|([Rr]ibbons)|([Ss]carpes)|([Rr]ibbons-sinister)|([Cc]hevronels)|([[Cc]hevronels-inverted)|([Ff]illet-crosses)|([Ff]illet-saltires)/ in (kwd);
    terminal compound_field ::= /([Bb]arry)|([Bb]arruly)|([Pp]aly)|([Bb]endy)|([Bb]endy-sinister)|([Cc]hevronny)|([Cc]hequy)|([Gg]yronny)|([Bb]arry-bendy)|([Ll]ozengy)/ in (kwd);

    terminal subordinary ::= /([Cc]hief)|([Bb]ase)|([Cc]anton)|([Cc]anton-sinister)|([Bb]ordure)|([Gg]yron)|([Oo]rle)|([Dd]ouble-tressure)|([Qq]uarter)|([Ll]abel)|([Pp]ile)|([Pp]ile-inverted)|([Pp]all)|([Ee]scutcheon)/ in (kwd);

    terminal single_geom_charge ::= /([Aa]nnulet)|([Bb]illet)|([Cc]artouche)|([Dd]elf)|([Ff]ret)|([Ff]usil)|([Ll]ozenge)|([Mm]ascle)|([Mm]ullet)|([Rr]oustre)|([Rr]oundel)|([Gg]oute)|([Ss]altorel)/ in (kwd);
    terminal compound_geom_charge ::= /([Aa]nnulets)|([Bb]illets)|([Cc]artouches)|([Dd]elves)|([Ff]rets)|([Ff]usils)|([Ll]ozenges)|([Mm]ascles)|([Mm]ullets)|([Rr]oustres)|([Rr]oundels)|([Gg]outes)|([Ss]altorels)/ in (kwd);
    terminal single_geom_charge_untinctured ::= /([Bb]ezant)|([Pp]late)|([Tt]orteau)|([Hh]urt)|([Pp]omme)|([Gg]olpe)|([Pp]ellet)|([Oo]range)|([Gg]uze)|([Ff]ountain)/ in (kwd);
    terminal compound_geom_charge_untinctured ::= /([Bb]ezants)|([Pp]lates)|([Tt]orteaux)|([Hh]urts)|([Pp]ommes)|([Gg]olpes)|([Pp]ellets)|([Oo]ranges)|([Gg]uzes)|([Ff]ountains)/ in (kwd);

    terminal locused_connection ::= /([Gg]rasping-with)|([Hh]olding-in)|([Hh]olding-with)/ in (kwd);
    terminal anchor_connection ::= /([Ee]mergent)|([Ii]ssuant)/ in (kwd);
    terminal stack_connection ::= /([Cc]harged-with)/ in (kwd);
    terminal topped_connection ::= /([Ss]urmounted-by)/ in (kwd);

    terminal quarterly ::= /([Qq]uarterly)/ in (kwd);

    terminal placed ::= /([Pp]laced)/ in (kwd);
    terminal within ::= /([Ww]ithin)/ in (kwd);

    terminal point ::= /([Cc]hief)|([Bb]ase)|([Dd]exter)|([Ss]inister)|([Ff]ess-point)|([Dd]exter-chief)|([[Ss]inister-chief)|([Dd]exter-base)|([Ss]inister-base)|([Dd]exter-flank)|([Ss]inister-flank)|([Mm]iddle-chief)|([Mm]iddle-base)|([Hh]ono(u)?r-point)|([Nn]ombril-point)/ in (kwd);

    terminal numeric_cardinal_enumeration ::= /[1-9][0-9]*/ in (enum);
    terminal digit_cardinal_enumeration ::= /([Oo]ne)|([Tt]wo)|([Tt]hree)|([Ff]our)|([Ff]ive)|([Ss]ix)|([Ss]even)|([Ee]ight)|([Nn]ine)/ in (enum);
    terminal teen_cardinal_enumeration ::= /([Tt]en)|([Ee]leven)|([Tt]welve)|([Tt]hirteen)|([Ff]ourteen)|([Ff]ifteen)|([Ss]ixteen)|([Ss]eventeen)|([Ee]ighteen)|([Nn]ineteen)/ in (enum);
    terminal tenmult_cardinal_enumeration ::= /([Tt]wenty)|([Tt]hirty)|([Ff]orty)|([Ff]ifty)|([Ss]ixty)|([Ss]eventy)|([Ee]ighty)|([Nn]inety)/ in (enum);

    // terminal cardinal_enumeration ::= /[:numeric_cardinal_enumeration:]|[:teen_cardinal_enumeration:]|(([:tenmult_cardinal_enumeration:]-)?[:digit_cardinal_enumeration:])/ in (enum);
    terminal cardinal_enumeration ::= /([1-9][0-9]*)|(([Tt]en)|([Ee]leven)|([Tt]welve)|([Tt]hirteen)|([Ff]ourteen)|([Ff]ifteen)|([Ss]ixteen)|([Ss]eventeen)|([Ee]ighteen)|([Nn]ineteen))|(((([Tt]wenty)|([Tt]hirty)|([Ff]orty)|([Ff]ifty)|([Ss]ixty)|([Ss]eventy)|([Ee]ighty)|([Nn]inety))-)?(([Oo]ne)|([Tt]wo)|([Tt]hree)|([Ff]our)|([Ff]ive)|([Ss]ix)|([Ss]even)|([Ee]ight)|([Nn]ine)))/ in (enum);

    terminal numeric_ordinal_enumeration ::= /([1-9][0-9]*)?((1st)|(2nd)|(3rd)|([4-9]th))/ in (enum);
    terminal digit_ordinal_enumeration ::= /([Ff]irst)|([Ss]econd)|([Tt]hird)|([Ff]ourth)|([Ff]ifth)|([Ss]ixth)|([Ss]eventh)|([Ee]ighth)|([Nn]inth)/ in (enum);
    terminal teen_ordinal_enumeration ::= /([Tt]enth)|([Ee]leventh)|([Tt]welfth)|([Tt]hirteenth)|([Ff]ourteenth)|([Ff]ifteenth)|([Ss]ixteenth)|([Ss]eventeenth)|([Ee]ighteenth)|([Nn]ineteenth)/ in (enum);
    terminal tenmult_ordinal_enumeration ::= /([Tt]wentieth)|([Tt]hirtieth)|([Ff]ortieth)|([Ff]iftieth)|([Ss]ixtieth)|([Ss]eventieth)|([Ee]ightieth)|([Nn]inetieth)/ in (enum);

    // terminal ordinal_enumeration ::= /[:numeric_ordinal_enumeration:]|[:teen_ordinal_enumeration:]|[:tenmult_ordinal_enumeration:]|(([:tenmult_cardinal_enumeration:]-)?[:digit_ordinal_enumeration:])/ in (enum);
    terminal ordinal_enumeration ::= /(([1-9][0-9]*)?((1st)|(2nd)|(3rd)|([4-9]th)))|(([Tt]enth)|([Ee]leventh)|([Tt]welfth)|([Tt]hirteenth)|([Ff]ourteenth)|([Ff]ifteenth)|([Ss]ixteenth)|([Ss]eventeenth)|([Ee]ighteenth)|([Nn]ineteenth))|(([Tt]wentieth)|([Tt]hirtieth)|([Ff]ortieth)|([Ff]iftieth)|([Ss]ixtieth)|([Ss]eventieth)|([Ee]ightieth)|([Nn]inetieth))|(((([Tt]wenty)|([Tt]hirty)|([Ff]orty)|([Ff]ifty)|([Ss]ixty)|([Ss]eventy)|([Ee]ighty)|([Nn]inety))-)?(([Ff]irst)|([Ss]econd)|([Tt]hird)|([Ff]ourth)|([Ff]ifth)|([Ss]ixth)|([Ss]eventh)|([Ee]ighth)|([Nn]inth)))/ in (enum);

    terminal object_charge ::= /[A-Za-z-]+/ in (general), < (kwd,enum);
    terminal object_attribute ::= /[A-Za-z-]+/ in (general), < (kwd,enum);
    // terminal charge_orientation ::= /[:fullsize_ordinary:]wise/ in (kwd);
    terminal charge_orientation ::= /(([Ff]ess)|([Pp]ale)|([Tt]ierce)|([Bb]end)|([Bb]end-sinister)|([Cc]hevron)|([Cc]hevron-inverted)|([Ss]altire)|([Cc]ross))wise/ in (kwd);
    terminal animal_charge_stance ::= /([Cc]oambulant)|([Cc]ombatant)|([Cc]ouchant)|([Cc]ourant)|([Dd]ormant)|([Gg]lisant)|([Nn]owed)|([Pp]assant)|([Rr]ampant)|([Ss]alient)|([Ss]ejant)|([Ss]tatant)|([Tt]rippant)|([Vv]olant)/ in (kwd);
    terminal animal_charge_position ::= /(([Gg]uardant)|([Rr]egardant))?/ in (general), < (kwd,enum);
    terminal image_charge ::= /image\:\[[A-Za-z0-9-_.]|(\\ )+\]/ in (general), < (kwd,enum);

    terminal per ::= /([Pp]arty )?[Pp]er/ in (kwd);
    terminal about ::= /[Aa]bout/ in (kwd);
    terminal between ::= /[Bb]etween/ in (kwd);
    terminal on ::= /[Oo]n/ in (kwd);
    terminal of ::= /[Oo]f/ in (kwd);
    terminal in ::= /[Ii]n/ in (kwd);
    terminal with ::= /[Ww]ith/ in (kwd);
    terminal from ::= /[Ff]rom/ in (kwd);
    terminal same ::= /[Ss]ame/ in (kwd);
    terminal to ::= /[Tt]o/ in (kwd);
    terminal the ::= /[Tt]he/ in (kwd);
    terminal and ::= /and/ in (kwd);
    terminal a ::= /a[n]?/ in (kwd);

    terminal comma ::= /,/;
    terminal semicolon ::= /;/;
    terminal colon ::= /:/ in (enum);
%lex}

%cf{
    precedence left in;
    precedence left colon;
    precedence left semicolon;
    precedence right stack_connection;
    precedence left comma;
    precedence left and;

    non terminal Blazon;
    non terminal Field;
    non terminal SimpleField;
    non terminal LocalFieldPointed;
    non terminal DividedField;
    non terminal ChargedField;
    non terminal CompoundField;
    non terminal Tincture;
    non terminal ChargeTincture;
    non terminal RasterChargeTincture;
    non terminal Tinctures;
    non terminal TincturesS;
    non terminal ChargeTincturesQ;
    non terminal RasterChargeTincturesQ;
    non terminal ChargeVector;
    non terminal ChargeRaster;
    non terminal ChargeOrdinary;
    non terminal ChargeSubordinary;
    non terminal ChargeGeometric;
    non terminal Charge;
    non terminal ChargeNeutral;
    non terminal ChargeStack;
    non terminal SingleOrdinary;
    non terminal PerOrdinary;
    non terminal ASingleOrdinary;
    non terminal SubOrdinary;
    non terminal ASubOrdinary;
    non terminal CompoundOrdinary;
    non terminal SingleGeomCharge;
    non terminal SingleGeomChargeUntinctured;
    non terminal CompoundGeomCharge;
    non terminal CompoundGeomChargeUntinctured;
    non terminal OrdinaryDecorated;
    non terminal HonorableOrdinaryDecorated;
    non terminal SubordinaryDecorated;
    non terminal ChargeAttributes;
    non terminal RasterChargeAttributes;
    non terminal RasterChargeAttributesP;
    non terminal ObjectAttributeList;
    non terminal ObjectAttributeListS;
    non terminal ChargeAdjectives;
    non terminal ChargeArrangement;
    non terminal EnumerationList;

    start with Blazon;

    Blazon ::= Field;

    Field ::= ChargedField;
    Field ::= DividedField;

    DividedField ::= PerOrdinary Field and Field;
    DividedField ::= PerOrdinary colon LocalFieldPointed;
    DividedField ::= CompoundField;

    ChargedField ::= Field comma Charge;
    ChargedField ::= Field comma ChargeSubordinary;

    CompoundField ::= SimpleField;
    CompoundField ::= compound_field line of cardinal_enumeration Tinctures;
    CompoundField ::= compound_field of cardinal_enumeration Tinctures;

    ChargeOrdinary ::= OrdinaryDecorated ChargeStack;
    ChargeOrdinary ::= about HonorableOrdinaryDecorated comma Charge;
    ChargeOrdinary ::= on OrdinaryDecorated comma Charge;
    ChargeOrdinary ::= on HonorableOrdinaryDecorated between Charge and Charge comma Charge;

    ChargeSubordinary ::= within SubordinaryDecorated ChargeStack;

    OrdinaryDecorated ::= HonorableOrdinaryDecorated;
    OrdinaryDecorated ::= SubordinaryDecorated;

    SubordinaryDecorated ::= ASubOrdinary ChargeAdjectives CompoundField ChargeAttributes;

    HonorableOrdinaryDecorated ::= ASingleOrdinary ChargeAdjectives CompoundField ChargeAttributes;
    HonorableOrdinaryDecorated ::= CompoundOrdinary Tincture;
    HonorableOrdinaryDecorated ::= CompoundOrdinary comma Tinctures;

    SimpleField ::= Tincture;

    LocalFieldPointed ::= in point DividedField semicolon LocalFieldPointed;
    LocalFieldPointed ::= and in point DividedField;

    Charge ::= in point ChargeNeutral;
    Charge ::= ChargeNeutral;

    ChargeNeutral ::= ChargeVector;
    ChargeNeutral ::= ChargeRaster;

    ChargeVector ::= ChargeGeometric;
    ChargeVector ::= ChargeOrdinary;

    ChargeGeometric ::= a SingleGeomCharge ChargeAdjectives charge_orientation ChargeTincture ChargeAttributes ChargeStack;
    ChargeGeometric ::= a SingleGeomCharge ChargeAdjectives ChargeTincture ChargeAttributes ChargeStack;
    ChargeGeometric ::= cardinal_enumeration CompoundGeomCharge ChargeAdjectives in ChargeArrangement ChargeTincturesQ ChargeAttributes;
    ChargeGeometric ::= cardinal_enumeration CompoundGeomCharge ChargeAdjectives ChargeTincturesQ ChargeAttributes;
    ChargeGeometric ::= a SingleGeomChargeUntinctured ChargeAdjectives comma ChargeAttributes ChargeStack;
    ChargeGeometric ::= a SingleGeomChargeUntinctured ChargeAdjectives charge_orientation comma ChargeAttributes ChargeStack;
    ChargeGeometric ::= cardinal_enumeration CompoundGeomChargeUntinctured ChargeAdjectives in ChargeArrangement comma ChargeAttributes;
    ChargeGeometric ::= cardinal_enumeration CompoundGeomChargeUntinctured ChargeAdjectives comma ChargeAttributes;

    ChargeRaster ::= a object_charge ChargeAdjectives RasterChargeTincture RasterChargeAttributesP ChargeStack;
    ChargeRaster ::= a object_charge ChargeAdjectives charge_orientation RasterChargeTincture RasterChargeAttributes ChargeStack;
    ChargeRaster ::= cardinal_enumeration object_charge ChargeAdjectives in ChargeArrangement RasterChargeTincturesQ RasterChargeAttributesP ChargeStack;
    ChargeRaster ::= cardinal_enumeration object_charge ChargeAdjectives RasterChargeTincture RasterChargeAttributesP ChargeStack;
    ChargeRaster ::= a object_charge animal_charge_stance animal_charge_position ChargeAdjectives RasterChargeTincture RasterChargeAttributes ChargeStack;
    ChargeRaster ::= cardinal_enumeration object_charge animal_charge_stance animal_charge_position ChargeAdjectives in ChargeArrangement RasterChargeTincturesQ RasterChargeAttributesP ChargeStack;
    ChargeRaster ::= cardinal_enumeration object_charge animal_charge_stance animal_charge_position ChargeAdjectives RasterChargeTincture RasterChargeAttributesP ChargeStack;
    ChargeRaster ::= image_charge charge_orientation RasterChargeTincture ChargeStack;

    ChargeStack ::= locused_connection the point object_attribute ChargeNeutral;
    ChargeStack ::= locused_connection the object_attribute ChargeNeutral;
    ChargeStack ::= anchor_connection from ChargeNeutral;
    ChargeStack ::= stack_connection ChargeNeutral;
    ChargeStack ::= between Charge and Charge;
    ChargeStack ::= ;

    ChargeAttributes ::= ;
    ChargeAttributes ::= ObjectAttributeList ChargeTincture ChargeAttributes;
    ChargeAttributes ::= with ObjectAttributeList to point ChargeAttributes;

    RasterChargeAttributesP ::= placed EnumerationList RasterChargeAttributes;
    RasterChargeAttributesP ::= RasterChargeAttributes;

    RasterChargeAttributes ::= ;
    RasterChargeAttributes ::= ObjectAttributeList RasterChargeTincture RasterChargeAttributes;
    RasterChargeAttributes ::= with ObjectAttributeList to point RasterChargeAttributes;

    ObjectAttributeListS ::= object_attribute comma ObjectAttributeListS;
    ObjectAttributeListS ::= object_attribute and object_attribute;

    ObjectAttributeList ::= object_attribute;
    ObjectAttributeList ::= ObjectAttributeListS;

    ChargeAdjectives ::= object_attribute ChargeAdjectives;
    ChargeAdjectives ::= ;

    EnumerationList ::= EnumerationList in point comma EnumerationList;
    EnumerationList ::= cardinal_enumeration comma EnumerationList;
    EnumerationList ::= cardinal_enumeration and cardinal_enumeration;

    Tinctures ::= TincturesS and Tincture;
    TincturesS ::= Tincture comma TincturesS;
    TincturesS ::= Tincture;

    ChargeTincturesQ ::= ChargeTincture;

    RasterChargeTincturesQ ::= RasterChargeTincture;

    RasterChargeTincture ::= proper;
    RasterChargeTincture ::= ChargeTincture;

    ChargeTincture ::= CompoundField;
    ChargeTincture ::= counterchanged;
    ChargeTincture ::= of the ordinal_enumeration;
    ChargeTincture ::= of the same;

    Tincture ::= color;
    Tincture ::= metal;
    Tincture ::= stain;
    Tincture ::= fur;

    ChargeArrangement ::= SingleOrdinary;
    ChargeArrangement ::= SubOrdinary;

    PerOrdinary ::= per SingleOrdinary;
    PerOrdinary ::= quarterly;

    ASingleOrdinary ::= a SingleOrdinary;

    SingleOrdinary ::= single_ordinary;
    SingleOrdinary ::= single_ordinary line;

    ASubOrdinary ::= a SubOrdinary;

    SubOrdinary ::= subordinary;
    SubOrdinary ::= subordinary line;

    CompoundOrdinary ::= cardinal_enumeration compound_ordinary;
    CompoundOrdinary ::= cardinal_enumeration compound_ordinary line;

    SingleGeomCharge ::= single_geom_charge;
    SingleGeomCharge ::= single_geom_charge line;

    SingleGeomChargeUntinctured ::= single_geom_charge_untinctured;
    SingleGeomChargeUntinctured ::= single_geom_charge_untinctured line;

    CompoundGeomCharge ::= compound_geom_charge;
    CompoundGeomCharge ::= compound_geom_charge line;

    CompoundGeomChargeUntinctured ::= compound_geom_charge_untinctured;
    CompoundGeomChargeUntinctured ::= compound_geom_charge_untinctured line;

%cf}

/*
  Abstract syntax
 Field is SimpleField
       or ChargedField
        
 SimpleField is PlainField
             or DividedField

 PlainField -> Tincture

 DividedField is DividedFieldByOrdinary
              or DividedFieldCompound

 DividedFieldByOrdinary -> divider::OrdinaryDirection
                           subfields::[Point :: Field]

 DividedFieldCompound -> number::int
                         direction::OrdinaryDirection
                         tinctures::[Tincture]

 ChargedField -> field::SimpleField
                 charge::Charge

 Charge is OrdinarySeparatedCharge
        or CompoundCharge
        or SingleCharge

 OrdinarySeparatedCharge -> ordinary::ChargeOrdinary
                            onDexter::Charge
                            onSinister::Charge

 CompoundCharge -> charges::[Charge]
                   layoutScheme::OrdinaryDirection

 SingleCharge is ChargeInanimate
              or ChargeAnimal
              or ChargeImage
              or ChargeOrdinary

 ChargeInanimate -> location::Subordinary
                    objectName::String
                    isInverted::boolean
                    orientation::OrdinaryDirection
                    tincture::ChargeTincture
                    adjectives::[String]
                    attributes::[String :: ChargeTincture]
                    stackedCharge::Charge

 ChargeAnimal -> animalName::String
                 stance::String
                 position::String
                 tincture::ChargeTincture
                 adjectives::[String]
                 attributes::[String :: ChargeTincture]
                 stackedCharge::Charge

 ChargeImage -> imageFileName::String
                orientation::OrdinaryDirection
                tincture::ChargeTincture
                stackedCharge::Charge
                

 ChargeOrdinary -> type::Ordinary
                   ordinaryField::Field

 ChargeTincture is Proper
                or Counterchanged
                or Tincture
 Proper -> eps
 Counterchanged -> eps


 Ordinary is HonorableOrdinary
          or Subordinary

 HonorableOrdinary -> number::int
                      type::OrdinaryDirection
                      line::Line
                      tinctures::[Tincture]

 Subordinary -> type::String
                line::Line
                tincture::Tincture

 OrdinaryDirection -> type::String

 Line -> type::String

 Point -> vertical::Ternary
          horizontal::Ternary

 Ternary is Negative (base/sinister)
         or Neutral (fesspoint)
         or Positive (chief/dexter)
 Negative -> eps
 Neutral -> eps
 Positive -> eps

 Tincture -> type::TinctureType
             name::String

 TinctureType is Metal
              or Color
              or Fur
              or Stain
 Metal -> eps
 Color -> eps
 Fur -> eps
 Stain -> eps
*/
