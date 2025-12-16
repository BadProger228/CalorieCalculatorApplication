USE [CalorieCalculatorDB]
GO

/****** Object:  Table [dbo].[saved_ration_items]    Script Date: 2025-12-16 2:25:35 AM ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

CREATE TABLE [dbo].[saved_ration_items](
	[id] [int] IDENTITY(1,1) NOT NULL,
	[saved_ration_id] [int] NOT NULL,
	[food_name] [nvarchar](200) NOT NULL,
	[calories] [float] NOT NULL,
	[protein] [float] NULL,
	[fat] [float] NULL,
	[carbs] [float] NULL,
	[weight_g] [int] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO

ALTER TABLE [dbo].[saved_ration_items] ADD  DEFAULT ((100)) FOR [weight_g]
GO

ALTER TABLE [dbo].[saved_ration_items]  WITH CHECK ADD  CONSTRAINT [FK_saved_ration_items_saved] FOREIGN KEY([saved_ration_id])
REFERENCES [dbo].[saved_rations] ([id])
ON DELETE CASCADE
GO

ALTER TABLE [dbo].[saved_ration_items] CHECK CONSTRAINT [FK_saved_ration_items_saved]
GO

