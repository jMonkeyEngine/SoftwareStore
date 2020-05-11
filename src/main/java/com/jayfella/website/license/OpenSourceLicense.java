package com.jayfella.website.license;

import com.fasterxml.jackson.annotation.JsonFormat;

import static com.jayfella.website.license.LicenseConditions.*;
import static com.jayfella.website.license.LicenseLimitations.*;
import static com.jayfella.website.license.LicensePermissions.Patent_Use;
import static com.jayfella.website.license.LicensePermissions.*;

@JsonFormat(shape= JsonFormat.Shape.OBJECT)
public enum OpenSourceLicense {

    None(
            "No license",
            "This software does not have a media license.",
            "https://choosealicense.com/no-permission/",
            new LicensePermissions[0],
            new LicenseConditions[0],
            new LicenseLimitations[0],
            LicenseTypes.Media
    ),

    Mixed_Software(
            "Mixed Licenses",
            "This software uses a mix of licenses. See description for details.",
            "",
            new LicensePermissions[0],
            new LicenseConditions[0],
            new LicenseLimitations[0],
            LicenseTypes.Software
    ),

    Mixed_Media(
            "Mixed Licenses",
            "The media contained in this software uses a mix of licenses. See description for details.",
            "",
            new LicensePermissions[0],
            new LicenseConditions[0],
            new LicenseLimitations[0],
            LicenseTypes.Media
    ),

    BSD_Zero_Clause(
            "BSD Zero Clause",
            "The BSD Zero Clause license goes further than the BSD 2-Clause license to allow you unlimited freedom with the software without requirements to include the copyright notice, license text, or disclaimer in either source or binary forms.",
            "https://opensource.org/licenses/0BSD",
            new LicensePermissions[] { Commercial_Use, Distribution, Modification, Private_Use },
            new LicenseConditions[0],
            new LicenseLimitations[] { Liability, Warranty },
            LicenseTypes.Software
    ),

    Academic_Free_v30(
            "Academic Free v3.0",
            "The Academic Free License is a variant of the Open Software License that does not require that the source code of derivative works be disclosed. It contains explicit copyright and patent grants and reserves trademark rights in the author.",
            "https://opensource.org/licenses/AFL-3.0",
            new LicensePermissions[] { Commercial_Use, Distribution, Modification, Patent_Use, Private_Use },
            new LicenseConditions[] { License_And_Copyright_Notice, State_Changes },
            new LicenseLimitations[] { Liability, Trademark_Use, Warranty },
            LicenseTypes.Software
    ),

    GNU_Affero_General_Public_v30(
            "GNU Affero General Public v3.0",
            "Permissions of this strongest copyleft license are conditioned on making available complete source code of licensed works and modifications, which include larger works using a licensed work, under the same license. Copyright and license notices must be preserved. Contributors provide an express grant of patent rights. When a modified version is used to provide a service over a network, the complete source code of the modified version must be made available. ",
            "https://opensource.org/licenses/AGPL-3.0",
            new LicensePermissions[] { Commercial_Use, Distribution, Modification, Patent_Use, Private_Use },
            new LicenseConditions[] { Disclose_Source, License_And_Copyright_Notice, Network_Use_Is_Distribution, Same_License, State_Changes },
            new LicenseLimitations[] { Liability, Warranty },
            LicenseTypes.Software
    ),

    Apache_20(
            "Apache 2.0",
            "A permissive license whose main conditions require preservation of copyright and license notices. Contributors provide an express grant of patent rights. Licensed works, modifications, and larger works may be distributed under different terms and without source code.",
            "https://www.apache.org/licenses/LICENSE-2.0",
            new LicensePermissions[] { Commercial_Use, Distribution, Modification, Patent_Use, Private_Use },
            new LicenseConditions[] { License_And_Copyright_Notice, State_Changes },
            new LicenseLimitations[] { Liability, Trademark_Use, Warranty },
            LicenseTypes.Software
    ),

    Artistic_20(
            "Artistic 2.0",
            "Heavily favored by the Perl community, the Artistic license requires that modified versions of the software do not prevent users from running the standard version.",
            "https://opensource.org/licenses/Artistic-2.0",
            new LicensePermissions[] { Commercial_Use, Distribution, Modification, Patent_Use, Private_Use },
            new LicenseConditions[] { License_And_Copyright_Notice, State_Changes },
            new LicenseLimitations[] { Liability, Trademark_Use, Warranty },
            LicenseTypes.Software
    ),

    BSD_2_Clause_Simplified(
            "BSD 2-Clause \"Simplified\"",
            "A permissive license that comes in two variants, the BSD 2-Clause and BSD 3-Clause. Both have very minute differences to the MIT license.",
            "https://opensource.org/licenses/BSD-2-Clause",
            new LicensePermissions[] { Commercial_Use, Distribution, Modification, Private_Use },
            new LicenseConditions[] { License_And_Copyright_Notice },
            new LicenseLimitations[] { Liability, Warranty },
            LicenseTypes.Software
    ),

    /*
    BSD_3_Clause_Clear(
            "BSD 3-Clause Clear",
            "A variant of the BSD 3-Clause License that explicitly does not grant any patent rights. ",
            "https://choosealicense.com/licenses/bsd-3-clause-clear/",
            new LicensePermissions[] { Commercial_Use, Distribution, Modification, Private_Use },
            new LicenseConditions[] { License_And_Copyright_Notice },
            new LicenseLimitations[] { Liability, LicenseLimitations.Patent_Use, Warranty }
    ),
     */

    BSD_3_Clause_New_Revised(
            "BSD 3-Clause “New” or “Revised”",
            "A permissive license similar to the BSD 2-Clause License, but with a 3rd clause that prohibits others from using the name of the project or its contributors to promote derived products without written consent.",
            "https://opensource.org/licenses/BSD-3-Clause",
            new LicensePermissions[] { Commercial_Use, Distribution, Modification, Private_Use },
            new LicenseConditions[] { License_And_Copyright_Notice },
            new LicenseLimitations[] { Liability, Warranty },
            LicenseTypes.Software
    ),

    Boost_Software_10(
            "Boost Software 1.0",
            "A simple permissive license only requiring preservation of copyright and license notices for source (and not binary) distribution. Licensed works, modifications, and larger works may be distributed under different terms and without source code.",
            "https://www.boost.org/LICENSE_1_0.txt",
            new LicensePermissions[] { Commercial_Use, Distribution, Modification, Private_Use },
            new LicenseConditions[] { License_And_Copyright_Notice },
            new LicenseLimitations[] { Liability, Warranty },
            LicenseTypes.Software
    ),

    Creative_Commons_Attribution_40_International(
            "Creative Commons Attribution 4.0 International",
            "Permits almost any use subject to providing credit and license notice. Frequently used for media assets and educational materials. The most common license for Open Access scientific publications. Not recommended for software.",
            "https://creativecommons.org/licenses/by/4.0/",
            new LicensePermissions[] { Commercial_Use, Distribution, Modification, Private_Use },
            new LicenseConditions[] { License_And_Copyright_Notice, State_Changes },
            new LicenseLimitations[] { Liability, LicenseLimitations.Patent_Use, Trademark_Use, Warranty },
            LicenseTypes.Media
    ),

    Creative_Commons_Attribution_Share_Alike_40_International(
            "Creative Commons Attribution Share Alike 4.0 International",
            "Similar to CC-BY-4.0 but requires derivatives be distributed under the same or a similar, compatible license. Frequently used for media assets and educational materials. A previous version is the default license for Wikipedia and other Wikimedia projects. Not recommended for software.",
            "https://creativecommons.org/licenses/by-sa/4.0/",
            new LicensePermissions[] { Commercial_Use, Distribution, Modification, Private_Use },
            new LicenseConditions[] { License_And_Copyright_Notice, Same_License, State_Changes },
            new LicenseLimitations[] { Liability, LicenseLimitations.Patent_Use, Trademark_Use, Warranty },
            LicenseTypes.Media
    ),

    Creative_Commons_Zero_10_Universal(
            "Creative Commons Zero v1.0 Universal",
            "The Creative Commons CC0 Public Domain Dedication waives copyright interest in a work you've created and dedicates it to the world-wide public domain. Use CC0 to opt out of copyright entirely and ensure your work has the widest reach. As with the Unlicense and typical software licenses, CC0 disclaims warranties. CC0 is very similar to the Unlicense.",
            "https://creativecommons.org/publicdomain/zero/1.0/",
            new LicensePermissions[] { Commercial_Use, Distribution, Modification, Private_Use },
            new LicenseConditions[] {  },
            new LicenseLimitations[] { Liability, LicenseLimitations.Patent_Use, Trademark_Use, Warranty },
            LicenseTypes.Media
    ),

    CC_BY_SA_30(
            "Creative Commons Attribution-ShareAlike 3.0 Unported",
            "A work licensed in this way grants all the four freedoms listed in the definition of free cultural works.",
            "https://creativecommons.org/licenses/by-sa/3.0/",
            new LicensePermissions[] { Commercial_Use, Distribution, Modification, Private_Use },
            new LicenseConditions[] { State_Changes, Give_Credit, Same_License },
            new LicenseLimitations[] { Liability, Warranty },
            LicenseTypes.Media
    ),

    Educational_Community_License_20(
            "Educational Community v2.0",
            "The Educational Community License version 2.0 (\"ECL\") consists of the Apache 2.0 license, modified to change the scope of the patent grant in section 3 to be specific to the needs of the education communities using this license.",
            "https://opensource.org/licenses/ECL-2.0",
            new LicensePermissions[] { Commercial_Use, Distribution, Modification, Patent_Use, Private_Use },
            new LicenseConditions[] { License_And_Copyright_Notice, State_Changes },
            new LicenseLimitations[] { Liability, Trademark_Use, Warranty },
            LicenseTypes.Software
    ),

    Eclipse_Public_10(
            "Eclipse Public 1.0",
            "This commercially-friendly copyleft license provides the ability to commercially license binaries; a modern royalty-free patent license grant; and the ability for linked works to use other licenses, including commercial ones.",
            "https://choosealicense.com/licenses/epl-1.0/",
            new LicensePermissions[] { Commercial_Use, Distribution, Modification, Patent_Use, Private_Use },
            new LicenseConditions[] { Disclose_Source, License_And_Copyright_Notice, Same_License },
            new LicenseLimitations[] { Liability, Warranty },
            LicenseTypes.Software
    ),

    Eclipse_Public_20(
            "Eclipse Public 2.0",
            "This commercially-friendly copyleft license provides the ability to commercially license binaries; a modern royalty-free patent license grant; and the ability for linked works to use other licenses, including commercial ones.",
            "https://www.eclipse.org/legal/epl-2.0/",
            new LicensePermissions[] { Commercial_Use, Distribution, Modification, Patent_Use, Private_Use },
            new LicenseConditions[] { Disclose_Source, License_And_Copyright_Notice, Same_License },
            new LicenseLimitations[] { Liability, Warranty },
            LicenseTypes.Software
    ),

    /*
    European_Union_Public_11(
            "European Union Public License 1.1",
            "The “European Union Public Licence” (EUPL) is a copyleft free/open source software license created on the initiative of and approved by the European Commission in 22 official languages of the European Union.",
            "https://choosealicense.com/licenses/eupl-1.1/",
            new LicensePermissions[] { Commercial_Use, Distribution, Modification, Patent_Use, Private_Use },
            new LicenseConditions[] { Disclose_Source, License_And_Copyright_Notice, Network_Use_Is_Distribution, Same_License, State_Changes },
            new LicenseLimitations[] { Liability, Trademark_Use, Warranty }
    ),
     */

    European_Union_Public_12(
            "European Union Public 1.2",
            "The European Union Public Licence (EUPL) is a copyleft free/open source software license created on the initiative of and approved by the European Commission in 22 official languages of the European Union.",
            "https://opensource.org/licenses/EUPL-1.2",
            new LicensePermissions[] { Commercial_Use, Distribution, Modification, Patent_Use, Private_Use },
            new LicenseConditions[] { Disclose_Source, License_And_Copyright_Notice, Network_Use_Is_Distribution, Same_License, State_Changes },
            new LicenseLimitations[] { Liability, Trademark_Use, Warranty },
            LicenseTypes.Software
    ),

    GNU_General_Public_20(
            "GNU General Public v2.0",
            "The GNU GPL is the most widely used free software license and has a strong copyleft requirement. When distributing derived works, the source code of the work must be made available under the same license. There are multiple variants of the GNU GPL, each with different requirements.",
            "https://www.gnu.org/licenses/old-licenses/gpl-2.0.en.html",
            new LicensePermissions[] { Commercial_Use, Distribution, Modification, Private_Use },
            new LicenseConditions[] { Disclose_Source, License_And_Copyright_Notice, Same_License, State_Changes },
            new LicenseLimitations[] { Liability, Warranty },
            LicenseTypes.Software
    ),

    GNU_General_Public_30(
            "GNU General Public v3.0",
            "Permissions of this strong copyleft license are conditioned on making available complete source code of licensed works and modifications, which include larger works using a licensed work, under the same license. Copyright and license notices must be preserved. Contributors provide an express grant of patent rights.",
            "https://www.gnu.org/licenses/gpl-3.0.en.html",
            new LicensePermissions[] { Commercial_Use, Distribution, Modification, Patent_Use, Private_Use },
            new LicenseConditions[] { Disclose_Source, License_And_Copyright_Notice, Same_License, State_Changes },
            new LicenseLimitations[] { Liability, Warranty },
            LicenseTypes.Software
    ),

    ISC(
            "ISC",
            "A permissive license lets people do anything with your code with proper attribution and without warranty. The ISC license is functionally equivalent to the BSD 2-Clause and MIT licenses, removing some language that is no longer necessary.",
            "https://opensource.org/licenses/ISC",
            new LicensePermissions[] { Commercial_Use, Distribution, Modification, Private_Use },
            new LicenseConditions[] { License_And_Copyright_Notice },
            new LicenseLimitations[] { Liability, Warranty },
            LicenseTypes.Software
    ),

    GNU_Lesser_General_Public_21(
            "GNU Lesser General Public v2.1",
            "Primarily used for software libraries, the GNU LGPL requires that derived works be licensed under the same license, but works that only link to it do not fall under this restriction. There are two commonly used versions of the GNU LGPL.",
            "https://www.gnu.org/licenses/old-licenses/lgpl-2.1.en.html",
            new LicensePermissions[] { Commercial_Use, Distribution, Modification, Private_Use },
            new LicenseConditions[] { Disclose_Source, License_And_Copyright_Notice, Same_License_Library, State_Changes },
            new LicenseLimitations[] { Liability, Warranty },
            LicenseTypes.Software
    ),

    GNU_Lesser_General_Public_30(
            "GNU Lesser General Public v3.0",
            "Permissions of this copyleft license are conditioned on making available complete source code of licensed works and modifications under the same license or the GNU GPLv3. Copyright and license notices must be preserved. Contributors provide an express grant of patent rights. However, a larger work using the licensed work through interfaces provided by the licensed work may be distributed under different terms and without source code for the larger work.",
            "https://www.gnu.org/licenses/lgpl-3.0.en.html",
            new LicensePermissions[] { Commercial_Use, Distribution, Modification, Patent_Use, Private_Use },
            new LicenseConditions[] { Disclose_Source, License_And_Copyright_Notice, Same_License_Library, State_Changes },
            new LicenseLimitations[] { Liability, Warranty },
            LicenseTypes.Software
    ),

    LaTeX_Project_Public_13c(
            "LaTeX Project Public v1.3c",
            "The LaTeX Project Public License (LPPL) is the primary license under which the LaTeX kernel and the base LaTeX packages are distributed.",
            "https://www.latex-project.org/lppl/",
            new LicensePermissions[] { Commercial_Use, Distribution, Modification, Private_Use },
            new LicenseConditions[] { Disclose_Source, License_And_Copyright_Notice, State_Changes },
            new LicenseLimitations[] { Liability, Warranty },
            LicenseTypes.Software
    ),

    MIT(
            "MIT",
            "A short and simple permissive license with conditions only requiring preservation of copyright and license notices. Licensed works, modifications, and larger works may be distributed under different terms and without source code.",
            "https://opensource.org/licenses/MIT",
            new LicensePermissions[] { Commercial_Use, Distribution, Modification, Private_Use },
            new LicenseConditions[] { License_And_Copyright_Notice },
            new LicenseLimitations[] { Liability, Warranty },
            LicenseTypes.Software
    ),

    Mozilla_Public_20(
            "Mozilla Public 2.0",
            "Permissions of this weak copyleft license are conditioned on making available source code of licensed files and modifications of those files under the same license (or in certain cases, one of the GNU licenses). Copyright and license notices must be preserved. Contributors provide an express grant of patent rights. However, a larger work using the licensed work may be distributed under different terms and without source code for files added in the larger work.",
            "https://www.mozilla.org/en-US/MPL/2.0/",
            new LicensePermissions[] { Commercial_Use, Distribution, Modification, Patent_Use, Private_Use },
            new LicenseConditions[] { Disclose_Source, License_And_Copyright_Notice, Same_License_File },
            new LicenseLimitations[] { Liability, Trademark_Use, Warranty },
            LicenseTypes.Software
    ),

    Microsoft_Public(
            "Microsoft Public",
            "An open source license with a patent grant. ",
            "https://choosealicense.com/licenses/ms-pl/",
            new LicensePermissions[] { Commercial_Use, Distribution, Modification, Patent_Use, Private_Use },
            new LicenseConditions[] { License_And_Copyright_Notice },
            new LicenseLimitations[] { Trademark_Use, Warranty },
            LicenseTypes.Software
    ),

    Microsoft_Reciprocal(
            "Microsoft Reciprocal",
            "An open source license with a patent grant similar to the Microsoft Public License, with the additional condition that any source code for any derived file be provided under this license.",
            "https://opensource.org/licenses/MS-PL",
            new LicensePermissions[] { Commercial_Use, Distribution, Modification, Patent_Use, Private_Use },
            new LicenseConditions[] { Disclose_Source, License_And_Copyright_Notice, Same_License_File },
            new LicenseLimitations[] { Trademark_Use, Warranty },
            LicenseTypes.Software
    ),


    University_of_Illinois_NCSA(
            "University of Illinois/NCSA Open Source",
            "The University of Illinois/NCSA Open Source License, or UIUC license, is a permissive free software license, based on the MIT/X11 license and the BSD 3-clause License. Its conditions include requiring the preservation of copyright and license notices both in source and in binary distributions and the prohibition of using the names of the authors or the project organization to promote or endorse derived products.",
            "https://opensource.org/licenses/NCSA",
            new LicensePermissions[] { Commercial_Use, Distribution, Modification, Private_Use },
            new LicenseConditions[] { License_And_Copyright_Notice },
            new LicenseLimitations[] { Liability, Warranty },
            LicenseTypes.Software
    ),

    /*
    ODC_Open_Database_10(
            "ODC Open Database License v1.0",
            "The Open Database License (ODbL) is a license agreement intended to allow users to freely share, modify, and use a database while maintaining this same freedom for others.",
            "https://opendatacommons.org/licenses/odbl/1-0/index.html",
            new LicensePermissions[] { Commercial_Use, Distribution, Modification, Private_Use },
            new LicenseConditions[] { Disclose_Source, License_And_Copyright_Notice, Same_License },
            new LicenseLimitations[] { Liability, LicenseLimitations.Patent_Use, Trademark_Use, Warranty },
            LicenseTypes.Software
    ),
     */

    SIL_Open_Font_11(
            "SIL Open Font 1.1",
            "The Open Font License (OFL) is maintained by SIL International. It attempts to be a compromise between the values of the free software and typeface design communities. It is used for almost all open source font projects, including those by Adobe, Google and Mozilla.",
            "https://opensource.org/licenses/OFL-1.1",
            new LicensePermissions[] { Commercial_Use, Distribution, Modification, Private_Use },
            new LicenseConditions[] { License_And_Copyright_Notice, Same_License },
            new LicenseLimitations[] { Liability, Warranty },
            LicenseTypes.Media
    ),

    Open_Software_30(
            "Open Software 3.0",
            "OSL 3.0 is a copyleft license that does not require reciprocal licensing on linked works. It also provides an express grant of patent rights from contributors to users, with a termination clause triggered if a user files a patent infringement lawsuit.",
            "https://opensource.org/licenses/OSL-3.0",
            new LicensePermissions[] { Commercial_Use, Distribution, Modification, Patent_Use, Private_Use },
            new LicenseConditions[] { Disclose_Source, License_And_Copyright_Notice, Network_Use_Is_Distribution, Same_License, State_Changes },
            new LicenseLimitations[] { Liability, Trademark_Use, Warranty },
            LicenseTypes.Software
    ),

    /*
    PostgreSQL(
            "PostgreSQL",
            "A very short, BSD-style license, used specifically for PostgreSQL.",
            "https://choosealicense.com/licenses/postgresql/",
            new LicensePermissions[] { Commercial_Use, Distribution, Modification, Private_Use },
            new LicenseConditions[] { License_And_Copyright_Notice },
            new LicenseLimitations[] { Liability, Warranty }
    ),
     */

    Unlicense(
            "The Unlicense",
            "A license with no conditions whatsoever which dedicates works to the public domain. Unlicensed works, modifications, and larger works may be distributed under different terms and without source code.",
            "https://unlicense.org/",
            new LicensePermissions[] { Commercial_Use, Distribution, Modification, Private_Use },
            new LicenseConditions[] {  },
            new LicenseLimitations[] { Liability, Warranty },
            LicenseTypes.Software
    ),

    Universal_Permissive_10(
            "Universal Permissive v1.0",
            "A permissive, OSI and FSF approved, GPL compatible license, expressly allowing attribution with just a copyright notice and a short form link rather than the full text of the license. Includes an express grant of patent rights. Licensed works and modifications may be distributed under different terms and without source code, and the patent grant may also optionally be expanded to larger works to permit use as a contributor license agreement.",
            "https://opensource.org/licenses/UPL",
            new LicensePermissions[] { Commercial_Use, Distribution, Modification, Patent_Use, Private_Use },
            new LicenseConditions[] { License_And_Copyright_Notice },
            new LicenseLimitations[] { Liability, Warranty },
            LicenseTypes.Software
    ),

    Do_What_The_Fck_You_Want_Public(
            "Do What The F*ck You Want To Public",
            "The easiest license out there. It gives the user permissions to do whatever they want with your code.",
            "http://www.wtfpl.net/about/",
            new LicensePermissions[] { Commercial_Use, Distribution, Modification, Private_Use },
            new LicenseConditions[] {  },
            new LicenseLimitations[] {  },
            LicenseTypes.Software
    ),

    zlib(
            "zlib",
            "A short permissive license, compatible with GPL. Requires altered source versions to be documented as such.",
            "https://choosealicense.com/licenses/zlib/",
            new LicensePermissions[] { Commercial_Use, Distribution, Modification, Private_Use },
            new LicenseConditions[] { License_And_Copyright_Notice, State_Changes },
            new LicenseLimitations[] { Liability, Warranty },
            LicenseTypes.Software
    ),

    Other(
            "Other",
            "See description for details",
            "",
            new LicensePermissions[0],
            new LicenseConditions[0],
            new LicenseLimitations[0],
            LicenseTypes.Software
    ),

    ;

    private final String name;
    private final String desc;
    private final String url;

    private final LicensePermissions[] permissions;
    private final LicenseConditions[] conditions;
    private final LicenseLimitations[] limitations;
    private final LicenseTypes type;

    OpenSourceLicense(String name, String desc, String url, LicensePermissions[] perms, LicenseConditions[] conditions, LicenseLimitations[] limitations, LicenseTypes type) {
        this.name = name;
        this.desc = desc;
        this.url = url;

        this.permissions = perms;
        this.conditions = conditions;
        this.limitations = limitations;
        this.type = type;
    }

    public String getName() { return name; }
    public String getDesc() { return desc; }
    public String getUrl() { return url; }
    public LicensePermissions[] getPermissions() { return permissions; }
    public LicenseConditions[] getConditions() { return conditions; }
    public LicenseLimitations[] getLimitations() { return limitations; }
    public LicenseTypes getLicenseType() { return type; }

    public static OpenSourceLicense fromString(String value) {

        if (value == null || value.isEmpty()) {
            return null;
        }

        for (OpenSourceLicense license : values()) {
            if (license.getName().equalsIgnoreCase(value)) {
                return license;
            }
        }

        return null;
    }

    @Override
    public String toString() {
        return this.getName();
    }
}
