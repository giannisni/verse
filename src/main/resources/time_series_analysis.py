import pandas as pd
from statsmodels.tsa.arima.model import ARIMA
import sys

def perform_time_series_analysis(df1, df2):
    # Rename 'count' to 'count1' and 'count2' in df1 and df2 respectively

    # print(df1)
    # print(df2)

    # Rename 'co
    df1.rename(columns={'count': 'count1'}, inplace=True)
    df2.rename(columns={'count': 'count2'}, inplace=True)



    # Sort the date indices
    df1.sort_index(inplace=True)
    df2.sort_index(inplace=True)

    df1 = df1.asfreq('D')
    df2 = df2.asfreq('D')

    df2.fillna(method='bfill', inplace=True)



    # Merge the two DataFrames on the date index
    merged_df = df1.merge(df2, how='inner', left_index=True, right_index=True)

    # Prepare the data for time series analysis
    data = merged_df[['count1', 'count2']]

    # Fit an ARIMA model to the data
    model = ARIMA(data['count1'], order=(1, 0, 0), exog=data['count2'])
    model_fit = model.fit()

    # Forecast the next step (next day)
    forecast = model_fit.forecast(steps=1, exog=[[data['count2'].iat[-1]]])


    # Extract the forecasted value
    forecasted_count = forecast[0]  # Access first element of the forecast array


    # Print the forecasted count
    print(forecasted_count)



    # Print the forecasted date

if __name__ == "__main__":
    file1 = sys.argv[1]
    file2 = sys.argv[2]

    df1 = pd.read_csv(file1, index_col=0, parse_dates=True)
    df2 = pd.read_csv(file2, index_col=0, parse_dates=True)

    perform_time_series_analysis(df1, df2)
