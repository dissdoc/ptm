class Message < ActiveRecord::Base
  belongs_to :to_user, :class_name => "User"
  belongs_to :from_user, :class_name => "User"

  validates :to_user_id, :from_user_id, :description, :presence => true
end
