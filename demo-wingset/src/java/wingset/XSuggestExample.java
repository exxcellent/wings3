/*
 * $Id: TextComponentExample.java 2750 2006-08-02 08:10:54Z hengels $
 * Copyright 2006 wingS development team.
 *
 * This file is part of wingS (http://wingsframework.org).
 *
 * wingS is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 *
 * Please see COPYING for the complete licence.
 */

package wingset;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.wings.SBorderLayout;
import org.wings.SComponent;
import org.wings.SDimension;
import org.wings.SGridLayout;
import org.wings.SLabel;
import org.wings.SPanel;
import org.wings.STextField;
import org.wingx.XSuggest;
import org.wingx.XSuggestDataSource;

/**
 * Example demonstrating the use of component XSuggest.
 * 
 * @author Christian Schyma, Stephan Schuster
 */
public class XSuggestExample extends WingSetPane {

    private static final long serialVersionUID = 14544074749281370L;

    protected SComponent createExample() {
        SPanel panel = new SPanel();
        panel.add(createXSuggestPanel(), SBorderLayout.CENTER);
        return panel;
    }

    private SPanel createXSuggestPanel() {
        SLabel info = new SLabel(
                "See tooltips on textfields for more information" +
                " about the settings made for each XSuggest field.");
        info.setPreferredSize(new SDimension(280, 100));
        info.setWordWrap(true);
        
        SGridLayout gridLayout = new SGridLayout(2);
        gridLayout.setHgap(10);
        gridLayout.setVgap(5);
        SPanel inputs = new SPanel(gridLayout);
        inputs.setPreferredSize(new SDimension(280, SDimension.AUTO_INT));
        
        inputs.add(new SLabel("Normal textfield:"));
        STextField normal = new STextField();
        normal.setColumns(30);
        normal.setToolTipText("Nothing happens!");
        inputs.add(normal);

        inputs.add(new SLabel("Enter a country: "));
        XSuggest country = new XSuggest();
        country.setDataSource(new CountriesOfTheWorld());
        country.setToolTipText("Settings:" +
                "\ndefault");
        inputs.add(country);

        inputs.add(new SLabel("Enter a country: "));
        XSuggest country1 = new XSuggest();
        country1.setDataSource(new CountriesOfTheWorld());
        country1.setMaxCacheEntries(10);
        country1.setToolTipText("Settings:" +
                "\nsetMaxCacheEntries(10)");
        inputs.add(country1);

        inputs.add(new SLabel("Enter a country: "));
        XSuggest country2 = new XSuggest();
        country2.setDataSource(new CountriesOfTheWorld());
        country2.setMaxResultsDisplayed(3);
        country2.setMinQueryLength(2);
        country2.setQueryDelay(1);
        country2.setAutoHighlight(false);
        country2.setUseShadow(true);
        country2.setForceSelection(true);
        country2.setToolTipText("Settings:" +
                "\nsetMaxResultsDisplayed(3)" +
                "\nsetMinQueryLength(2)" +
                "\nsetQueryDelay(1)" +
                "\nsetAutoHighlight(false)" +
                "\nsetUseShadow(true)" +
                "\nsetForceSelection(true)");
        inputs.add(country2);
        
        inputs.add(new SLabel("Enter a country: "));
        XSuggest country3 = new XSuggest();
        country3.setDataSource(new CountriesOfTheWorld());
        country3.setTypeAhead(true);
        country3.setAllowBrowserAutocomplete(false);
        country3.setToolTipText("Settings:" +
                "\nsetTypeAhead(true)" +
                "\nsetAllowBrowserAutocomplete(false)");
        inputs.add(country3);

        inputs.add(new SLabel("Enter a german\nstate (try \"b\"): "));
        XSuggest states = new XSuggest();
        states.setDataSource(new StatesOfGermany());
        states.setToolTipText("Settings:" +
                "\nspecial datasource");
        inputs.add(states);
        
        SPanel panel = new SPanel(new SBorderLayout());
        panel.add(info, SBorderLayout.NORTH);
        panel.add(inputs, SBorderLayout.CENTER);
        panel.setPreferredSize(SDimension.FULLAREA);

        return panel;
    }

    protected SComponent createControls() {
        return null;
    }

    /**
     * a model for XSuggest
     * 
     * @author Roman Rädle
     * @version $Revision$
     */
    public class CountriesOfTheWorld implements XSuggestDataSource {

        private List<String> completion = new ArrayList<String>();

        public CountriesOfTheWorld() {
            init();
        }

        private void init() {
            completion.add("Afghanistan");
            completion.add("Albania");
            completion.add("Algeria");
            completion.add("American Samoa");
            completion.add("Andorra");
            completion.add("Angola");
            completion.add("Anguilla");
            completion.add("Antarctica");
            completion.add("Antigua & Barbuda");
            completion.add("Antilles, Netherlands");
            completion.add("Arabia, Saudi");
            completion.add("Argentina");
            completion.add("Armenia");
            completion.add("Aruba");
            completion.add("Asia Pacific");
            completion.add("Australia");
            completion.add("Austria");
            completion.add("Azerbaijan");
            completion.add("Bahamas, The");
            completion.add("Bahrain");
            completion.add("Bangladesh");
            completion.add("Barbados");
            completion.add("Belarus");
            completion.add("Belgium");
            completion.add("Belize");
            completion.add("Benin");
            completion.add("Bermuda");
            completion.add("Bhutan");
            completion.add("Bolivia");
            completion.add("Bosnia and Herzegovina");
            completion.add("Botswana");
            completion.add("Bouvet Island");
            completion.add("Brazil");
            completion.add("British Indian Ocean Territory");
            completion.add("British Virgin Islands");
            completion.add("Brunei Darussalam");
            completion.add("Bulgaria");
            completion.add("Burkina Faso");
            completion.add("Burundi");
            completion.add("Cambodia");
            completion.add("Cameroon");
            completion.add("Canada");
            completion.add("Cape Verde");
            completion.add("Cayman Islands");
            completion.add("Central African Republic");
            completion.add("Chad");
            completion.add("Chile");
            completion.add("China");
            completion.add("Christmas Island");
            completion.add("Cocos (Keeling) Islands");
            completion.add("Colombia");
            completion.add("Comoros");
            completion.add("Congo");
            completion.add("Congo, Democratic Rep. of the");
            completion.add("Cook Islands");
            completion.add("Costa Rica");
            completion.add("Cote D'Ivoire");
            completion.add("Croatia");
            completion.add("Cuba");
            completion.add("Cyprus");
            completion.add("Czech Republic");
            completion.add("Czechoslovakia (Former)");
            completion.add("Denmark");
            completion.add("Djibouti");
            completion.add("Dominica");
            completion.add("Dominican Republic");
            completion.add("East Timor (Timor-Leste)");
            completion.add("Ecuador");
            completion.add("Egypt");
            completion.add("El Salvador");
            completion.add("Equatorial Guinea");
            completion.add("Eritrea");
            completion.add("Estonia");
            completion.add("Ethiopia");
            completion.add("European Union");
            completion.add("Falkland Islands (Malvinas)");
            completion.add("Faroe Islands");
            completion.add("Fiji");
            completion.add("Finland");
            completion.add("France");
            completion.add("French Guiana");
            completion.add("French Polynesia");
            completion.add("French Southern Territories - TF");
            completion.add("Gabon");
            completion.add("Gambia, the");
            completion.add("Georgia");
            completion.add("Germany");
            completion.add("Ghana");
            completion.add("Gibraltar");
            completion.add("Greece");
            completion.add("Greenland");
            completion.add("Grenada");
            completion.add("Guadeloupe");
            completion.add("Guam");
            completion.add("Guatemala");
            completion.add("Guerney");
            completion.add("Guinea");
            completion.add("Guinea-Bissau");
            completion.add("Guinea, Equatorial");
            completion.add("Guiana, French");
            completion.add("Guyana");
            completion.add("Haiti");
            completion.add("Heard and McDonald Islands");
            completion.add("Holy See (Vatican City State)");
            completion.add("Holland (see Netherlands)");
            completion.add("Honduras");
            completion.add("Hong Kong, (China)");
            completion.add("Hungary");
            completion.add("Iceland");
            completion.add("India");
            completion.add("Indonesia");
            completion.add("Iran, Islamic Republic of");
            completion.add("Iraq");
            completion.add("Ireland");
            completion.add("Israel");
            completion.add("Ivory Coast (see Cote d'Ivoire)");
            completion.add("Italy");
            completion.add("Jamaica");
            completion.add("Japan");
            completion.add("Jersey");
            completion.add("Jordan");
            completion.add("Kazakhstan");
            completion.add("Kenya");
            completion.add("Kiribati");
            completion.add("Korea, Demo. People's Rep. of");
            completion.add("Korea, (South) Republic of");
            completion.add("Kuwait");
            completion.add("Kyrgyzstan");
            completion.add("Lao People's Democratic Republic");
            completion.add("Latvia");
            completion.add("Lebanon");
            completion.add("Lesotho");
            completion.add("Liberia");
            completion.add("Libyan Arab Jamahiriya");
            completion.add("Liechtenstein");
            completion.add("Lithuania");
            completion.add("Luxembourg");
            completion.add("Macao, (China)");
            completion.add("Macedonia, TFYR");
            completion.add("Madagascar");
            completion.add("Malawi");
            completion.add("Malaysia");
            completion.add("Maldives");
            completion.add("Mali");
            completion.add("Malta");
            completion.add("Marshall Islands");
            completion.add("Martinique");
            completion.add("Mauritania");
            completion.add("Mauritius");
            completion.add("Mayotte");
            completion.add("Mexico");
            completion.add("Micronesia, Federated States of");
            completion.add("Moldova, Republic of");
            completion.add("Monaco");
            completion.add("Mongolia");
            completion.add("Montserrat");
            completion.add("Morocco");
            completion.add("Mozambique");
            completion.add("Myanmar (ex-Burma)");
            completion.add("Namibia");
            completion.add("Nauru");
            completion.add("Nepal");
            completion.add("Netherlands");
            completion.add("Netherlands Antilles");
            completion.add("New Caledonia");
            completion.add("New Zealand");
            completion.add("Nicaragua");
            completion.add("Niger");
            completion.add("Nigeria");
            completion.add("Niue");
            completion.add("Norfolk Island");
            completion.add("Northern Mariana Islands");
            completion.add("Norway");
            completion.add("Oman");
            completion.add("Pakistan");
            completion.add("Palau");
            completion.add("Palestinian Territory");
            completion.add("Panama");
            completion.add("Papua New Guinea");
            completion.add("Paraguay");
            completion.add("Peru");
            completion.add("Philippines");
            completion.add("Pitcairn Island");
            completion.add("Poland");
            completion.add("Portugal");
            completion.add("Puerto Rico");
            completion.add("Qatar");
            completion.add("Reunion");
            completion.add("Romania");
            completion.add("Russia (Russian Federation)");
            completion.add("Rwanda");
            completion.add("Sahara");
            completion.add("Saint Helena");
            completion.add("Saint Kitts and Nevis");
            completion.add("Saint Lucia");
            completion.add("Saint Pierre and Miquelon");
            completion.add("Saint Vincent and the Grenadines");
            completion.add("Samoa");
            completion.add("San Marino");
            completion.add("Sao Tome and Principe");
            completion.add("Saudi Arabia");
            completion.add("Senegal");
            completion.add("Serbia & Montenegro");
            completion.add("Seychelles");
            completion.add("Sierra Leone");
            completion.add("Singapore");
            completion.add("Slovakia");
            completion.add("Slovenia");
            completion.add("Solomon Islands");
            completion.add("Somalia");
            completion.add("South Africa");
            completion.add("S. Georgia and S. Sandwich Is.");
            completion.add("Spain");
            completion.add("Sri Lanka (ex-Ceilan)");
            completion.add("Sudan");
            completion.add("Suriname");
            completion.add("Svalbard and Jan Mayen Islands");
            completion.add("Swaziland");
            completion.add("Sweden");
            completion.add("Switzerland");
            completion.add("Syrian Arab Republic");
            completion.add("Taiwan");
            completion.add("Tajikistan");
            completion.add("Tanzania, United Republic of");
            completion.add("Thailand");
            completion.add("Timor-Leste (East Timor)");
            completion.add("Togo");
            completion.add("Tokelau");
            completion.add("Tonga");
            completion.add("Trinidad & Tobago");
            completion.add("Tunisia");
            completion.add("Turkey");
            completion.add("Turkmenistan");
            completion.add("Turks and Caicos Islands");
            completion.add("Tuvalu");
            completion.add("Uganda");
            completion.add("Ukraine");
            completion.add("United Arab Emirates");
            completion.add("United Kingdom");
            completion.add("United States");
            completion.add("US Minor Outlying Islands");
            completion.add("Uruguay");
            completion.add("Uzbekistan");
            completion.add("Vanuatu");
            completion.add("Vatican City State (Holy See)");
            completion.add("Venezuela");
            completion.add("Viet Nam");
            completion.add("Virgin Islands, British");
            completion.add("Virgin Islands, U.S.");
            completion.add("Wallis and Futuna");
            completion.add("Western Sahara");
            completion.add("Yemen");
            completion.add("Zambia");
            completion.add("Zimbabwe");
        }

        public List<Map.Entry<String, String>> generateSuggestions(String part) {
            List<Map.Entry<String, String>> returning = new ArrayList<Map.Entry<String, String>>();
            for (Iterator<String> iter = completion.iterator(); iter.hasNext();) {
                String s = iter.next();
                if (s.toLowerCase().startsWith(part.toLowerCase())) {
                    returning.add(new Entry(s, s));
                }
            }
            
            if (returning.isEmpty()) {
                String noData = "No matches found!";
                returning.add(new Entry(noData, noData));
            }

            return returning;
        }

    }

    /**
     * another model for XSuggest
     * 
     * @author Christian Schyma
     * @version $Revision$
     */
    public class StatesOfGermany implements XSuggestDataSource {

        class State {
            public String name;
            public String iso;

            State(String name, String iso) {
                this.name = name;
                this.iso = iso;
            }
        }

        private List<State> states = new ArrayList<State>();

        public StatesOfGermany() {
            init();
        }

        private void init() {
            states.add(new State("Berlin", "DE-BE"));
            states.add(new State("Brandenburg", "DE-BR"));
            states.add(new State("Baden-Württemberg", "DE-BW"));
            states.add(new State("Bayern", "DE-BY"));
            states.add(new State("Bremen", "DE-HB"));
            states.add(new State("Hessen", "DE-HE"));
            states.add(new State("Hamburg", "DE-HH"));
            states.add(new State("Mecklenburg-Vorpommern", "DE-MV"));
            states.add(new State("Niedersachsen", "DE-NI"));
            states.add(new State("Nordrhein-Westfalen", "DE-NW"));
            states.add(new State("Rheinland-Pfalz", "DE-RP"));
            states.add(new State("Schleswig-Holstein", "DE-SH"));
            states.add(new State("Saarland", "DE-SL"));
            states.add(new State("Sachsen", "DE-SN"));
            states.add(new State("Sachsen-Anhalt", "DE-ST"));
            states.add(new State("Thüringen", "DE-TH"));
        }

        public List<Map.Entry<String, String>> generateSuggestions(String part) {
            List<Map.Entry<String, String>> returning = new ArrayList<Map.Entry<String, String>>();
            for (Iterator<State> iter = states.iterator(); iter.hasNext();) {
                State state = iter.next();
                if (state.name.toLowerCase().startsWith(part.toLowerCase())) {
                    returning.add(new Entry(state.name, state.iso + " "
                            + state.name));
                }
            }
            
            if (returning.isEmpty()) {
                String noData = "No matches found!";
                returning.add(new Entry(noData, noData));
            }

            return returning;
        }
    }

}
