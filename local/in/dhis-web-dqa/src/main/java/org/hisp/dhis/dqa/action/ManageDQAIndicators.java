package org.hisp.dhis.dqa.action;

/*
 * Copyright (c) 2004-2012, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * * Neither the name of the HISP project nor the names of its contributors may
 *   be used to endorse or promote products derived from this software without
 *   specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.hisp.dhis.common.comparator.IdentifiableObjectNameComparator;
import org.hisp.dhis.indicator.IndicatorService;
import org.hisp.dhis.indicator.Indicator;
import com.opensymphony.xwork2.Action;

public class ManageDQAIndicators
    implements Action
{
	 // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

	IndicatorService indicatorService;
	
    public void setIndicatorService(IndicatorService indicatorService) 
	{
		this.indicatorService = indicatorService;
	}

    // -------------------------------------------------------------------------
    // Input/Output
    // -------------------------------------------------------------------------

    private List<Indicator> indicators = new ArrayList<Indicator>();

    public List<Indicator> getIndicators()
    {
        return indicators;
    }
    
    private List<Indicator> chosenIndicators = new ArrayList<Indicator>();
    
    public List<Indicator> getChosenIndicators() {
		return chosenIndicators;
	}

	private String parameterName;
    public String getParameterName() {
		return parameterName;
	}

	public void setParameterName(String parameterName) {
		this.parameterName = parameterName;
	}

	private String parameterValue;
    
	public void setParameterValue(String parameterValue) {
		this.parameterValue = parameterValue;
	}

	public String execute()
    {
		System.out.println(parameterName+ " - " + parameterValue);
		indicators=(List<Indicator>) indicatorService.getAllIndicators();
		  Collections.sort( indicators, IdentifiableObjectNameComparator.INSTANCE );
	
		  if (parameterValue!=null){
			  String[] valueArray = parameterValue.split(",");
			  for(int i= 0;i<valueArray.length ;i++){
				  System.out.println(valueArray[i]);
				  Indicator indicator =indicatorService.getIndicator(Integer.parseInt(valueArray[i]));
				  System.out.println("indicator="+ indicator);
				  if(indicator!=null)
				  chosenIndicators.add(indicator);
			  }
		  }
		  System.out.println("chosenindi size="+chosenIndicators.size());
        return SUCCESS;
    }
}
