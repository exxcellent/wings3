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

import java.util.*;

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
 * @author Christian Schyma
 */
public class XSuggestExample extends WingSetPane {
    
    XSuggest birthCountryField = null;
    XSuggest currentCountryField = null;
    
    protected SComponent createExample() {
        SPanel panel = new SPanel();
        panel.add(createXSuggestPanel(), SBorderLayout.CENTER);        
        return panel;
    }
    
    private SPanel createXSuggestPanel() {     
        SGridLayout gridLayout = new SGridLayout(2);
        gridLayout.setHgap(10);
        gridLayout.setVgap(4);
        SPanel panel = new SPanel(gridLayout);        
        
        panel.add(new SLabel("Surname:"));
        STextField surname = new STextField();
        surname.setToolTipText("enter your surname");
        panel.add(surname);
        
        panel.add(new SLabel("Name:"));
        STextField name = new STextField();
        name.setToolTipText("enter your name");
        panel.add(name);        
        
        panel.add(new SLabel("Birth Country:"));
        birthCountryField = new XSuggest();
        birthCountryField.setDataSource(new CountriesOfTheWorld());        
        birthCountryField.setToolTipText("where have you been born?");
        birthCountryField.setSuggestBoxWidth(new SDimension(SDimension.INHERIT, SDimension.INHERIT));
        panel.add(birthCountryField);        
        
        panel.add(new SLabel("Current Country:"));
        currentCountryField = new XSuggest();
        currentCountryField.setDataSource(new CountriesOfTheWorld());        
        currentCountryField.setToolTipText("where are you living?");
        currentCountryField.setSuggestBoxWidth(new SDimension(SDimension.INHERIT, SDimension.INHERIT));
        panel.add(currentCountryField);        
        
        panel.add(new SLabel("State (slow answer, only German: try 'b'): "));
        XSuggest stateSuggestionField = new XSuggest();
        stateSuggestionField.setDataSource(new StatesOfGermany());                        
        panel.add(stateSuggestionField);        
        
        panel.add(new SLabel("auto correct: "));
        XSuggest autoCorrect = new XSuggest();                
        panel.add(autoCorrect);
                
        // set width of text fields
        for (int i = 0; i < panel.getComponentCount(); i++) {
            SComponent component = panel.getComponent(i);
            if ((component instanceof XSuggest) || (component instanceof  STextField)) {
                ((STextField)component).setColumns(20);
            }
        }                
        
        return panel;
    }

    protected SComponent createControls() {
        return null;
    }
    
    /**     
     * a model for XSuggest
     * @author Roman R&auml;dle
     * @version $Revision$
     */
    public class CountriesOfTheWorld implements XSuggestDataSource {
        
        private List completion;

        public CountriesOfTheWorld() {
            completion = new ArrayList();
            initCompletion();
        }

        private String ajaxText;

        public String getAjaxText() {
            return ajaxText;
        }

        public void setAjaxText(String ajaxText) {
            this.ajaxText = ajaxText;
        }

        private void initCompletion() {
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
        
        public List<Map.Entry<String,String>> generateSuggestions(String part) {
            List<Map.Entry<String,String>> returning = new ArrayList<Map.Entry<String,String>>();
            for (Iterator iter = completion.iterator(); iter.hasNext();) {                
                Object o = iter.next();
                if (o instanceof String) {
                    String s = (String)o;
                    if (s.toLowerCase().startsWith(part.toLowerCase())) {
                        returning.add(new Entry(s, s));
                    }
                }
            }            
                       
            return returning;
        }
                
    }
    
    /**     
     * another model for XSuggest 
     * @author Christian Schyma
     * @version $Revision$
     */
    public class StatesOfGermany implements XSuggestDataSource {
        
        private String ajaxText;

        public String getAjaxText() {
            return ajaxText;
        }

        public void setAjaxText(String ajaxText) {
            this.ajaxText = ajaxText;
        }

        class State {
            public String name;
            public String iso;
            
            State(String name, String iso) {
                this.name = name;
                this.iso = iso;
            }            
        }
        
        private List states = new ArrayList();
        
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
                 
        public List<Map.Entry<String,String>> generateSuggestions(String part) {
            List<Map.Entry<String,String>> returning = new ArrayList<Map.Entry<String,String>>();
            for (Iterator iter = states.iterator(); iter.hasNext();) {                
                Object o = iter.next();                
                if (o instanceof State) {
                    State state = (State)o;
                    if (state.name.toLowerCase().startsWith(part.toLowerCase())) {
                        returning.add(new Entry(state.name, state.iso + " " + state.name));
                    }
                }
            }
            
            // simulate slow answer
            try {                
                Thread.sleep(2000);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            
            return returning;
        }
                
        /** Creates a new instance of StatesOfGermany */
        public StatesOfGermany() {            
            init();
        }
    }
    
}
