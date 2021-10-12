//
//  ContentView.swift
//  iOS
//
//  Created by Jerry Okafor on 11/10/2021.
//

import SwiftUI

struct FoodItem : Codable {
    let id: Int
    let productName: String
    let image: String
    let from:String
    let nutrients:String
    let quantity:String
    let price:String
    let organic:Bool
    let description:String
}


struct ContentView: View {
    @State
    var foods = [FoodItem]()
    
    var body: some View {
          
       return  NavigationView{
        GeometryReader{geo in
            ZStack(alignment: .center){
                VStack(alignment: .leading,spacing: 16) {
                    ForEach(foods,id: \.id){food in
                        return NavigationLink(
                            destination: FoodDetails(food: food)){
                            HStack(alignment: .center, spacing: 20){
                                Text("\(food.image)")
                                    .bold().frame(width:50, height: 50, alignment: .center)
                                    .scaleEffect(2.0)
                                Text("\(food.productName)")
                                    .bold()
                                    .padding(.leading,10)
                                Spacer(minLength: 20)
                                Text("₦ \(food.price)")
                                    .bold()
                                    .padding(.leading,20)
                            }.frame(maxWidth:.infinity)
                            .padding(.horizontal,20)
                        }.buttonStyle(PlainButtonStyle())
                    }

                }.frame(width: geo.size.width, height: geo.size.width, alignment: .topLeading)
            }
            .navigationBarTitle("Foods")
        }
       }.onAppear(perform: {loadJson()})
    }
    
    func loadJson() {
       let decoder = JSONDecoder()
       guard let url = Bundle.main.url(forResource: "data", withExtension: "json"),
             let data = try? Data(contentsOf: url)
        else {
            return
       }

        
        let foods = try? decoder.decode([FoodItem].self, from: data)
        self.foods = foods!
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
