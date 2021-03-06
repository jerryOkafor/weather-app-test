//
//  FoodDetails.swift
//  iOS
//
//  Created by Jerry Okafor on 12/10/2021.
//

import SwiftUI


struct FoodDetails : View {
    var food:FoodItem
    
    var body: some View{
        GeometryReader{geo in
            ScrollView(/*@START_MENU_TOKEN@*/.vertical/*@END_MENU_TOKEN@*/, showsIndicators: false, content: {
                VStack(alignment: /*@START_MENU_TOKEN@*/.center/*@END_MENU_TOKEN@*/, spacing: 20){
                    Text(food.image)
                        .frame(width: 250, height: 250, alignment: .center)
                            .scaleEffect(10.0)
                   
                    HStack(alignment:.top){
                        VStack(alignment: .leading, spacing: 0){
                            Text("\(food.nutrients)").bold().font(.title)
                           
                        }
                        Spacer(minLength: 20)
                        Text("₦ \(food.price)").bold().font(.title)
                    }.padding(.horizontal,20)
                    
                    HStack(alignment: .center, spacing: 20, content: {
                        VStack(alignment: .leading, spacing: 8, content: {
                            Text("From").foregroundColor(.gray)
                            Text("\(food.from)")
                        })
                        Spacer()
                        VStack(alignment: .center, spacing: 8, content: {
                            Text("Quantity").foregroundColor(.gray)
                            Text("\(food.quantity)")
                        })
                        
                        Spacer()
                        Text("\(food.organic ? "Organic" : "Inorganic")")
                    }).padding(.horizontal,20)
                    
                    VStack(alignment: .leading, spacing: 8, content: {
                        Text("Description").foregroundColor(.gray)
                        Text(food.description)
                    }).padding(.horizontal,20)
                    
                }.navigationBarTitle(self.food.productName)
                .frame(width: geo.size.width, height:geo.size.height, alignment: .top)
            })
        }
        
    }
    
}
