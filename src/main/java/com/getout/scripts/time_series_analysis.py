import pandas as pd
from statsmodels.tsa.arima.model import ARIMA
import sys

def perform_time_series_analysis(df1, df2):
    # Merge the two DataFrames on the date index
    merged_df = df1.merge(df2, how='inner', left_index=True, right_index=True)

    # Prepare the data for time series analysis
    data = merged_df[['count1', 'count2']]

    # Fit an ARIMA model to the data
    model = ARIMA(data['count1'], order=(1, 0, 0), exog=data['count2'])
    model_fit = model.fit()

    # Forecast the next step (next day)
    forecast = model_fit.forecast(steps=1, exog=data['count2'][-1:])

    # Extract the forecasted value
    forecasted_count = forecast[0][0]

    # Print the forecasted count
    print(forecasted_count)

if __name__ == "__main__":
    file1 = sys.argv[1]
    file2 = sys.argv[2]

    df1 = pd.read_csv(file1, index_col=0, parse_dates=True)
    df2 = pd.read_csv(file2, index_col=0, parse_dates=True)

    # Replace the existing index with the new DateTimeIndex
    df1.index = pd.DatetimeIndex(df1.index.values, freq=df1.index.inferred_freq)
    df2.index = pd.DatetimeIndex(df2.index.values, freq=df2.index.inferred_freq)


perform_time_series_analysis(df1, df2)
