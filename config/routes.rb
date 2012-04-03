Ptm::Application.routes.draw do
  match '/auth/:provider/callback', :to => 'accounts#create'

  devise_for :users, :controllers => { :registrations => 'auths' }
  resources :accounts

  resources :albums do
    get 'my', :on => :collection
    get 'share'

    resources :photos do
      resources :notes
    end
  end

  resources :friendlists
  match '/friendlists/add_friend', :to => 'friendlists#add_friend', :via => :post
  match '/friendlists/remove_friend', :to => 'friendlists#remove_friend', :via => :post

  root :to => 'home#index'
  match 'users', :to => 'home#users'
  match 'myfriends', :to => 'home#myfriends'
  match 'faq', :to => 'home#faq'
  match 'contacts', :to => 'home#contacts'
  match 'about', :to => 'home#about'
end
